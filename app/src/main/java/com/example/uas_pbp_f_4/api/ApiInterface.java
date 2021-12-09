package com.example.uas_pbp_f_4.api;

import com.example.uas_pbp_f_4.model.Books;
import com.example.uas_pbp_f_4.model.BooksResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @Headers({"Accept: application/json"})
    @GET("books")
    Call<BooksResponse> getAllBooks();

    @Headers({"Accept: application/json"})
    @GET("books/{userID}")
    Call<BooksResponse> getBooksByUserID(@Path("userID") String userID);

    @Headers({"Accept: application/json"})
    @POST("books")
    Call<BooksResponse> createBooks(@Body Books books);

    @Headers({"Accept: application/json"})
    @PUT("books/{id}")
    Call<BooksResponse> updateBooks(@Path("id") long id, @Body Books books);

    @Headers({"Accept: application/json"})
    @DELETE("books/{id}")
    Call<BooksResponse> deleteBooks(@Path("id") long id);
}
