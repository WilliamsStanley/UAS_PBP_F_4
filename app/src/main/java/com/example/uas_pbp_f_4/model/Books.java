package com.example.uas_pbp_f_4.model;

import android.widget.ImageView;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "books")
public class Books {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "ISBN")
    @SerializedName("ISBN")
    private String ISBN;

    @ColumnInfo(name = "nama_buku")
    @SerializedName("nama_buku")
    private String nama_buku;

    @ColumnInfo(name = "genre")
    @SerializedName("genre")
    private String genre;

    @ColumnInfo(name = "imgURL")
    @SerializedName("imgURL")
    private String imgURL;

    @ColumnInfo(name = "userID")
    private String userID;

    public Books() {

    }

    public Books(String ISBN, String nama_buku, String genre, String imgURL, String userID) {
        this.ISBN = ISBN;
        this.nama_buku = nama_buku;
        this.genre = genre;
        this.imgURL = imgURL;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String user_id) {
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getNama_buku() {
        return nama_buku;
    }

    public void setNama_buku(String nama_buku) {
        this.nama_buku = nama_buku;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImgURL() { return imgURL; }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public static void loadImage(ImageView imageView, String imgURL) {
        Glide.with(imageView)
                .load(imgURL)
                .into(imageView);
    }
}