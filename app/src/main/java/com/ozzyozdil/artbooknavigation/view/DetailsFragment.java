package com.ozzyozdil.artbooknavigation.view;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ozzyozdil.artbooknavigation.R;
import com.ozzyozdil.artbooknavigation.databinding.FragmentDetailsBinding;
import com.ozzyozdil.artbooknavigation.model.Art;
import com.ozzyozdil.artbooknavigation.roomdb.ArtDao;
import com.ozzyozdil.artbooknavigation.roomdb.ArtDatabase;

import java.io.ByteArrayOutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DetailsFragment extends Fragment {

    SQLiteDatabase database;
    Bitmap selectedImage;
    String info = "";
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private FragmentDetailsBinding binding;
    private final CompositeDisposable disposable = new CompositeDisposable();
    ArtDatabase artDatabase;
    ArtDao artDao;
    Art artFromMain;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerLauncher();

        artDatabase = Room.databaseBuilder(requireContext(), ArtDatabase.class, "Arts").build();
        artDao = artDatabase.artDao();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Menu yu kaldırıyor
        /*
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.removeItem(R.id.add_img);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false; //report no menu selection handled
            }
        });
         */

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = requireActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);

        if (getArguments() != null){
            info = DetailsFragmentArgs.fromBundle(getArguments()).getInfo();
        }
        else{
            info = "new";
        }

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(view);
            }
        });

        if (info.equals("new")) {
            binding.etxtArt.setText("");
            binding.etxtArtist.setText("");
            binding.etxtYear.setText("");
            binding.btnSave.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.GONE);

            binding.imageView.setImageResource(R.drawable.selectedimage);

        } else {
            int artId = DetailsFragmentArgs.fromBundle(getArguments()).getArtId();
            binding.btnSave.setVisibility(View.GONE);
            binding.btnDelete.setVisibility(View.VISIBLE);

            disposable.add(artDao.getArtById(artId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(DetailsFragment.this :: handleResponseWithOldArt));
        }
    }

    private void handleResponseWithOldArt(Art art){

        artFromMain = art;
        binding.etxtArt.setText(art.artname);
        binding.etxtArtist.setText(art.artistName);
        binding.etxtYear.setText(art.year);

        Bitmap bitmap = BitmapFactory.decodeByteArray(art.image, 0, art.image.length);
        binding.imageView.setImageBitmap(bitmap);
    }

    public void selectImage(View view){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view, "Fotoğraf eklemek için galeri erişimi gerekiyor!", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }
            else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else{
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

    }

    public void save(View view){

        String nameArt = binding.etxtArt.getText().toString();
        String nameArtist = binding.etxtArtist.getText().toString();
        String year = binding.etxtYear.getText().toString();


        Bitmap smallImage = makeSmallerImage(selectedImage, 1000);

        // Görseli SQL için veriye çevirme
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        // Verileri database e kaydetme
        Art art = new Art(nameArt, nameArtist, year, byteArray);

        disposable.add(artDao.insert(art)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DetailsFragment.this::handleResponse));

    }

    public void delete(View view) {
        disposable.add(artDao.delete(artFromMain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DetailsFragment.this::handleResponse));
    }

    private void handleResponse(){
        // Save butonuna tıkladıktan sonra ana ekrana dönmek için
        NavDirections action = DetailsFragmentDirections.actionDetailsFragmentToFirstFragment();
        Navigation.findNavController(requireView()).navigate(action);
    }

    // Activity Result Launcher
    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                        Uri imageData = intentFromResult.getData();
                        try {

                            ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), imageData);
                            selectedImage = ImageDecoder.decodeBitmap(source);
                            binding.imageView.setImageBitmap(selectedImage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else{
                    Toast.makeText(getActivity(), "İzin Gerekli!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Bitmap makeSmallerImage(@NonNull Bitmap image, int maxSize){

        int width =image.getWidth();
        int height =image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1){

            // Landscape image
            width = maxSize;
            height = (int) (width / bitmapRatio);
        }
        else{

            // Portrait image
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        disposable.clear();
    }

}