package com.example.uas_pbp_f_4.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.uas_pbp_f_4.model.Books;

import java.util.List;

@Dao
public interface BooksDao {
    @Query("SELECT * FROM Books")
    List<Books> getAll();

    @Insert
    void insertBooks(Books books);

    @Delete
    void deleteBooks(Books books);

    @Query("SELECT * FROM Books where userID = :userID")
    List<Books> getBooksByUserID(int userID);
}
