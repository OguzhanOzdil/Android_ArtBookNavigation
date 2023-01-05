package com.ozzyozdil.artbooknavigation.model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Art {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String artname;

    @Nullable
    @ColumnInfo(name = "artistname")
    public String artistName;

    @Nullable
    @ColumnInfo(name = "year")
    public String year;

    @Nullable
    @ColumnInfo(name = "image")
    public byte[] image;

    // Constructor
    public Art(String artname, @Nullable String artistName, @Nullable String year, @Nullable byte[] image) {
        this.artname = artname;
        this.artistName = artistName;
        this.year = year;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtname() {
        return artname;
    }

    public void setArtname(String artname) {
        this.artname = artname;
    }

    @Nullable
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(@Nullable String artistName) {
        this.artistName = artistName;
    }

    @Nullable
    public String getYear() {
        return year;
    }

    public void setYear(@Nullable String year) {
        this.year = year;
    }

    @Nullable
    public byte[] getImage() {
        return image;
    }

    public void setImage(@Nullable byte[] image) {
        this.image = image;
    }
}
