package com.example.uas_pbp_f_4.books;


import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uas_pbp_f_4.R;
import com.example.uas_pbp_f_4.api.ApiClient;
import com.example.uas_pbp_f_4.api.ApiInterface;
import com.example.uas_pbp_f_4.database.DatabaseClient;

import com.example.uas_pbp_f_4.databinding.ActivityRecylerViewAdapterBinding;
import com.example.uas_pbp_f_4.model.Books;
import com.example.uas_pbp_f_4.model.BooksResponse;
import com.example.uas_pbp_f_4.preferences.UserPreferences;
import com.example.uas_pbp_f_4.ui.auth.Register;
import com.example.uas_pbp_f_4.ui.books.TampilDataBuku;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.viewHolder> {
    public static String TAG;
    List<DataBuku> BukuList;
    private DatabaseClient databaseClient;
    private Context context;
    private List<Books> booksList;
    private UserPreferences userPreferences;
    private NotificationManagerCompat notificationManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private TampilDataBuku tampilDataBuku;
    private ApiInterface apiService;

    public RecyclerViewAdapter(List<DataBuku> BukuList, Context context, TampilDataBuku tampilDataBuku){
        this.tampilDataBuku = tampilDataBuku;
        this.BukuList = BukuList;
        this.context = context;
        this.userPreferences = new UserPreferences(context);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ActivityRecylerViewAdapterBinding activityRecylerViewAdapterBinding = ActivityRecylerViewAdapterBinding.inflate(layoutInflater, parent, false);

        notificationManager = NotificationManagerCompat.from(context);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new viewHolder(activityRecylerViewAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DataBuku dataBuku = BukuList.get(position);

        holder.activityRecyclerViewAdapterBinding.setBuku(dataBuku);
        holder.activityRecyclerViewAdapterBinding.executePendingBindings();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        holder.activityRecyclerViewAdapterBinding.btnPinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!firebaseUser.isEmailVerified()) {
                    Toast.makeText(context.getApplicationContext(), "Please verify your email first", Toast.LENGTH_SHORT).show();
                } else {
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
                    materialAlertDialogBuilder.setTitle("Konfirmasi")
                            .setMessage("Apakah anda yakin ingin meminjam buku ini?")
                            .setNegativeButton("Batal", null)
                            .setPositiveButton("Pinjam", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            firebaseAuth = FirebaseAuth.getInstance();
                                            apiService = ApiClient.getClient().create(ApiInterface.class);

                                            Books books = new Books(
                                                    dataBuku.getISBN(),
                                                    dataBuku.getNama_buku(),
                                                    dataBuku.getGenre(),
                                                    dataBuku.getImgURL(),
                                                    firebaseAuth.getCurrentUser().getUid());
                                            Call<BooksResponse> call = apiService.createBooks(books);

                                            call.enqueue(new Callback<BooksResponse>() {
                                                @Override
                                                public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                                                    if (response.isSuccessful()) {
                                                        Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        try {
                                                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                            Toast.makeText(view.getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                                                        } catch (Exception e) {
                                                            Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<BooksResponse> call, Throwable t) {
                                                    Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                            .show();
                }

//                Notification notification = new NotificationCompat.Builder(context, CHANNEL_2_ID)
//                        .setSmallIcon(R.drawable.logo)
//                        .setContentTitle("Pengembalian")
//                        .setContentText("Berhasil mengembalikan buku")
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                        .build();
//
//                notificationManager.notify(2, notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return BukuList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        ActivityRecylerViewAdapterBinding activityRecyclerViewAdapterBinding;

        public viewHolder(@NonNull ActivityRecylerViewAdapterBinding activityRecyclerViewAdapterBinding) {
            super(activityRecyclerViewAdapterBinding.getRoot());
            this.activityRecyclerViewAdapterBinding = activityRecyclerViewAdapterBinding;

            databaseClient = DatabaseClient.getInstance(context);
        }
    }
}
