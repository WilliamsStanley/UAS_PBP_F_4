package com.example.uas_pbp_f_4.books;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uas_pbp_f_4.MainActivity;
import com.example.uas_pbp_f_4.R;
import com.example.uas_pbp_f_4.database.DatabaseClient;
import com.example.uas_pbp_f_4.databinding.ActivityRecylerViewAdapterBinding;
import com.example.uas_pbp_f_4.databinding.MybooksBinding;
import com.example.uas_pbp_f_4.model.Books;
import com.example.uas_pbp_f_4.preferences.UserPreferences;
import com.example.uas_pbp_f_4.ui.books.MyBooksFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> implements Filterable {
    private List<Books> booksList, filteredBooksList;
    private Context context;
    private NotificationManagerCompat notificationManager;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private MyBooksFragment myBooksFragment;

    public BooksAdapter(List<Books> booksList, Context context, MyBooksFragment myBooksFragment) {
        this.myBooksFragment = myBooksFragment;
        this.booksList = booksList;
        filteredBooksList = new ArrayList<>(booksList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mybooks, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Books books = filteredBooksList.get(position);
        holder.tvISBN.setText(books.getISBN());
        holder.tvNama_buku.setText(books.getNama_buku());
        holder.tvGenre.setText(books.getGenre());
        Glide.with(context).load(books.getImgURL()).into(holder.tvImgURL);

        holder.btnKembalikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder =
                        new MaterialAlertDialogBuilder(context);
                materialAlertDialogBuilder.setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin mengembalikan buku ini?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Kembalikan", new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        myBooksFragment.deleteBooks(books.getId());

                                    }
                                })
                        .show();

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
        return filteredBooksList.size();
    }

    public void setBooksList(List<Books> booksList) {
        this.booksList = booksList;
        filteredBooksList = new ArrayList<>(booksList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charSequenceString = charSequence.toString();
                List<Books> filtered = new ArrayList<>();

                if (charSequenceString.isEmpty()) {
                    filtered.addAll(booksList);
                } else {
                    for (Books books : booksList) {
                        if (books.getNama_buku().toLowerCase()
                                .contains(charSequenceString.toLowerCase()))
                            filtered.add(books);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults
                    filterResults) {
                filteredBooksList.clear();
                filteredBooksList.addAll((List<Books>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvISBN, tvNama_buku, tvGenre;
        private ImageView tvImgURL;
        private MaterialButton btnKembalikan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvISBN = itemView.findViewById(R.id.tvISBN);
            tvNama_buku = itemView.findViewById(R.id.tvNamaBuku);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvImgURL = itemView.findViewById(R.id.tvImgURL);
            btnKembalikan = itemView.findViewById(R.id.btnKembalikan);

        }
    }
}