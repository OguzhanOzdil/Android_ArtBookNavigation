package com.ozzyozdil.artbooknavigation.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ozzyozdil.artbooknavigation.R;
import com.ozzyozdil.artbooknavigation.databinding.RecyclerRowBinding;
import com.ozzyozdil.artbooknavigation.model.Art;
import com.ozzyozdil.artbooknavigation.view.DetailsFragmentDirections;
import com.ozzyozdil.artbooknavigation.view.FirstFragment;
import com.ozzyozdil.artbooknavigation.view.FirstFragmentDirections;
import com.ozzyozdil.artbooknavigation.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

    List<Art> artList;

    public ArtAdapter (List<Art> artList){
        this.artList = artList;
    }

    public class ArtHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public ArtHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArtHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.textView.setText(artList.get(position).getArtName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaPlayer ses = MediaPlayer.create(holder.itemView.getContext(), R.raw.water);
                ses.start();

                FirstFragmentDirections.ActionFirstFragmentToDetailsFragment action = FirstFragmentDirections.actionFirstFragmentToDetailsFragment("old");
                action.setArtId(artList.get(position).getId());
                action.setInfo("old");
                Navigation.findNavController(view).navigate(action);

            }
        });

    }

    @Override
    public int getItemCount() {
        return artList.size();
    }

}
