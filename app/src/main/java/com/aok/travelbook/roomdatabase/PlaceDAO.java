package com.aok.travelbook.roomdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.aok.travelbook.model.Place;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

@Dao
public interface PlaceDAO {
    // DAO: Data Access Object

    @Query("SELECT * FROM Place")
    Flowable<List<Place>> getAll();

    @Insert
    Completable insert(Place place);

    @Delete
    Completable delete(Place place);



}
