package com.example.uas_pbp_f_4.books;

import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class DataBuku extends BaseObservable{
    public String ISBN, nama_buku, genre, imgURL;

    public DataBuku (String ISBN, String nama_buku, String genre, String imgURL) {
        this.ISBN = ISBN;
        this.nama_buku = nama_buku;
        this.genre = genre;
        this.imgURL = imgURL;
    }

    @Bindable
    public String getISBN() { return ISBN; }

    public void setISBN( String ISBN ) { this.ISBN = ISBN; }

    @Bindable
    public String getNama_buku() { return nama_buku; }

    public void setNama_buku( String nama_buku ) { this.nama_buku = nama_buku; }

    @Bindable
    public String getGenre() { return genre; }

    public void setGenre( String genre ) { this.genre = genre; }

    @Bindable
    public String getImgURL() { return imgURL; }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    @BindingAdapter("android:loadImage")
    public static void loadImage(ImageView imageView, String imgURL) {
        Glide.with(imageView)
                .load(imgURL)
                .into(imageView);
    }
}
