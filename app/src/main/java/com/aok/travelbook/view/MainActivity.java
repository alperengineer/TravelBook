package com.aok.travelbook.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.aok.travelbook.R;
import com.aok.travelbook.adapter.PlaceAdapter;
import com.aok.travelbook.databinding.ActivityMainBinding;
import com.aok.travelbook.model.Place;
import com.aok.travelbook.roomdatabase.PlaceDAO;
import com.aok.travelbook.roomdatabase.PlaceDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CompositeDisposable compositeDisposible = new CompositeDisposable();
    PlaceDatabase placeDatabase;
    PlaceDAO placeDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        placeDatabase = Room.databaseBuilder(getApplicationContext(), PlaceDatabase.class, "Place")
                .build();
        placeDAO = placeDatabase.placeDAO();

        compositeDisposible.add(placeDAO.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainActivity.this::handleResponse));


    }

    private void handleResponse(List<Place> placeList){
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        PlaceAdapter placeAdapter = new PlaceAdapter(placeList);
        binding.recyclerView.setAdapter(placeAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_place){
            Intent intent =new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("info", "new");

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.travel_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
}