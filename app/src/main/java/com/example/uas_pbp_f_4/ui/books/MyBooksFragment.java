package com.example.uas_pbp_f_4.ui.books;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uas_pbp_f_4.QRScannerActivity;
import com.example.uas_pbp_f_4.R;
import com.example.uas_pbp_f_4.api.ApiClient;
import com.example.uas_pbp_f_4.api.ApiInterface;
import com.example.uas_pbp_f_4.books.BooksAdapter;
import com.example.uas_pbp_f_4.model.Books;
import com.example.uas_pbp_f_4.model.BooksResponse;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBooksFragment extends Fragment {
    public static final int LAUNCH_ADD_ACTIVITY = 123;
    private RecyclerView rv_booksList;
    private BooksAdapter booksAdapter;
    private List<Books> books;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private String userID;
    private FirebaseAuth firebaseAuth;
    private ApiInterface apiService;
    private SwipeRefreshLayout srBooks;
    private String userId;
    private MyBooksFragment myBooksFragment;
    private TextView tvISBN, tvNama_buku, tvGenre;
    private ImageView tvImgURL;
    private MaterialButton btnKembalikan;

    private final ActivityResultLauncher<Intent> cameraResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                try {
                                    String ISBN, nama_buku, genre, imgURL;
                                    Intent intent = result.getData();
                                    String strQRRes = intent.getStringExtra("QR_RESULT");

                                    String[] res = strQRRes.split(";");

                                    ISBN = res[0];
                                    nama_buku = res[1];
                                    genre = res[2];
                                    imgURL = res[3];

                                    firebaseAuth = FirebaseAuth.getInstance();
                                    apiService = ApiClient.getClient().create(ApiInterface.class);

                                    Books books = new Books(
                                            ISBN,
                                            nama_buku,
                                            genre,
                                            imgURL,
                                            firebaseAuth.getCurrentUser().getUid());

                                    Call<BooksResponse> call = apiService.createBooks(books);

                                    call.enqueue(new Callback<BooksResponse>() {
                                        @Override
                                        public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                    Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<BooksResponse> call, Throwable t) {
                                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(requireContext(), "QR CODE TIDAK VALID!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_mybooks, container, false);

        tvISBN = root.findViewById(R.id.tvISBN);
        tvNama_buku = root.findViewById(R.id.tvNamaBuku);
        tvGenre = root.findViewById(R.id.tvGenre);
        tvImgURL = root.findViewById(R.id.tvImgURL);
        btnKembalikan = root.findViewById(R.id.btnKembalikan);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();

        apiService = ApiClient.getClient().create(ApiInterface.class);

        srBooks = root.findViewById(R.id.sr_books);
        srBooks.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBooksByUserID(userId);
            }
        });

        FloatingActionButton btnQr = root.findViewById(R.id.btnQr);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

//        btnQr.setOnClickListener(v -> {
//            if(!firebaseUser.isEmailVerified()){
//                Toast.makeText(getContext(), "Please verify your email first", Toast.LENGTH_SHORT).show();
//            } else {
//                cameraResult.launch(new Intent(getContext(), QRScannerActivity.class));
//            }
//        });

        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!firebaseUser.isEmailVerified()){
                    Toast.makeText(getContext(), "Please verify your email first", Toast.LENGTH_SHORT).show();
                } else {
                    cameraResult.launch(new Intent(getContext(), QRScannerActivity.class));
                }
            }
        });

        RecyclerView rv_bookList = root.findViewById(R.id.rv_booksList);

        booksAdapter = new BooksAdapter(new ArrayList<>(), getContext(), this);
        rv_bookList.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false));
        rv_bookList.setAdapter(booksAdapter);
        getBooksByUserID(userId);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(requestCode == LAUNCH_ADD_ACTIVITY && resultCode == Activity.RESULT_OK)
            getBooksByUserID(userId);
    }

    public void getBooksByUserID(String userId) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
        Call<BooksResponse> call = apiService.getBooksByUserID(userId);
        srBooks.setRefreshing(true);

        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                if(response.isSuccessful()) {
                    booksAdapter.setBooksList(response.body().getBooksList());
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                srBooks.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                srBooks.setRefreshing(false);
            }
        });
    }

    public void deleteBooks(int id) {
        Call<BooksResponse> call = apiService.deleteBooks(id);
        srBooks.setRefreshing(true);
        setLoading(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();

        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call,
                                   Response<BooksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    getBooksByUserID(userId);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error",
                        Toast.LENGTH_SHORT).show();
                setLoading(false);
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}