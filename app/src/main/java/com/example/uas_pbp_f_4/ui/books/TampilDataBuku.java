package com.example.uas_pbp_f_4.ui.books;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.uas_pbp_f_4.R;
import com.example.uas_pbp_f_4.api.ApiClient;
import com.example.uas_pbp_f_4.api.ApiInterface;
import com.example.uas_pbp_f_4.books.DaftarBuku;
import com.example.uas_pbp_f_4.books.DataBuku;
import com.example.uas_pbp_f_4.books.RecyclerViewAdapter;
import com.example.uas_pbp_f_4.databinding.FragmentTampilDataBukuBinding;
import com.example.uas_pbp_f_4.model.Books;
import com.example.uas_pbp_f_4.model.BooksResponse;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TampilDataBuku extends Fragment {
    private TampilDataBuku tampilDataBuku;
    private TextView tvISBN, tvNama_buku, tvGenre;
    private ImageView tvImgURL;
    private MaterialButton btnKembalikan;
    private ApiInterface apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentTampilDataBukuBinding fragmentTampilDataBukuBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_tampil_data_buku, container, false);
        View view = fragmentTampilDataBukuBinding.getRoot();

        tvISBN = view.findViewById(R.id.tvISBN);
        tvNama_buku = view.findViewById(R.id.tvNamaBuku);
        tvGenre = view.findViewById(R.id.tvGenre);
        tvImgURL = view.findViewById(R.id.tvImgURL);
        btnKembalikan = view.findViewById(R.id.btnKembalikan);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(DaftarBuku.getDataBuku(), this.getContext(), tampilDataBuku);
        fragmentTampilDataBukuBinding.rvBuku.setAdapter(recyclerViewAdapter);

        return view;
    }
}
