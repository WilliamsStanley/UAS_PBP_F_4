package com.example.uas_pbp_f_4.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BooksResponse {
    private String message;

    @SerializedName("data")
    private List<Books> booksList;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Books> getBooksList() {
        return booksList;
    }

    public void setBooksList(List<Books> booksList) {
        this.booksList = booksList;
    }
}
