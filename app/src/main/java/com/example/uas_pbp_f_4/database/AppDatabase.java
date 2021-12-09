package com.example.uas_pbp_f_4.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.uas_pbp_f_4.dao.BooksDao;
import com.example.uas_pbp_f_4.model.Books;

@Database(entities = {Books.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BooksDao todoDao();
}
