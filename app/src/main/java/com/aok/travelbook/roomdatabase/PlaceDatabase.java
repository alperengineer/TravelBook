package com.aok.travelbook.roomdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.aok.travelbook.model.Place;

@Database(entities = {Place.class}, version = 1)
public abstract class PlaceDatabase extends RoomDatabase {

    public abstract PlaceDAO placeDAO();

}
