package com.ozzyozdil.artbooknavigation.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ozzyozdil.artbooknavigation.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_img){

            // action
            FirstFragmentDirections.ActionFirstFragmentToDetailsFragment action = FirstFragmentDirections.actionFirstFragmentToDetailsFragment("new");
            Navigation.findNavController(this, R.id.fragmentContainerView).navigate(action);

        }
        return super.onOptionsItemSelected(item);
    }

}