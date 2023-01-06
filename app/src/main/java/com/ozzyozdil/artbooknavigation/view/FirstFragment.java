package com.ozzyozdil.artbooknavigation.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ozzyozdil.artbooknavigation.adapter.ArtAdapter;
import com.ozzyozdil.artbooknavigation.databinding.FragmentFirstBinding;
import com.ozzyozdil.artbooknavigation.model.Art;
import com.ozzyozdil.artbooknavigation.roomdb.ArtDao;
import com.ozzyozdil.artbooknavigation.roomdb.ArtDatabase;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    ArtAdapter artAdapter;
    ArtDatabase artDatabase;
    ArtDao artDao;

    private final CompositeDisposable disposable = new CompositeDisposable();

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       artDatabase = Room.databaseBuilder(requireContext(), ArtDatabase.class, "Arts").build();
       artDao = artDatabase.artDao();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        getData();
    }

    private void getData(){

        disposable.add(artDao.getArtWithNameAndId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FirstFragment.this :: handleResponse));
    }

    private void handleResponse(List<Art> artList) {

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        artAdapter = new ArtAdapter(artList);
        binding.recyclerView.setAdapter(artAdapter);

        // txt_Description (açıklama) yalnızca FirstFragment boş olursa gözükecek
        if (Objects.requireNonNull(binding.recyclerView.getAdapter()).getItemCount() != 0){
            binding.txtDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        disposable.clear();
    }

}