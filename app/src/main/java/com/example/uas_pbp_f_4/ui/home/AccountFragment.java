package com.example.uas_pbp_f_4.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uas_pbp_f_4.EditProfileActivity;
import com.example.uas_pbp_f_4.R;
import com.example.uas_pbp_f_4.preferences.UserPreferences;
import com.example.uas_pbp_f_4.ui.auth.Login;
import com.example.uas_pbp_f_4.ui.auth.Register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.Executor;
import java.util.zip.Inflater;

public class AccountFragment extends Fragment {
    private TextView tvID, tvUsername, tvEmail, tvVerify;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private Button btnVerify, btnLogout, btnEdit;
    private UserPreferences userPreferences;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        tvID = root.findViewById(R.id.tvID);
        tvUsername = root.findViewById(R.id.tvUsername);
        tvEmail = root.findViewById(R.id.tvEmail);
        tvVerify = root.findViewById(R.id.tvVerify);
        btnVerify = root.findViewById(R.id.btnVerify);
        btnLogout = root.findViewById(R.id.btnLogout);
        btnEdit = root.findViewById(R.id.btnEdit);
        
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        context = getContext();

        if(!firebaseUser.isEmailVerified()) {
            tvVerify.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.VISIBLE);

            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Please verify your email address", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure : Failed to sent Email" + e.getMessage());
                        }
                    });
                }
            });
        }

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    tvID.setText(userId);
                    tvUsername.setText(value.getString("Username"));
                    tvEmail.setText(value.getString("Email"));
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), Login.class));
                getActivity().finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!firebaseUser.isEmailVerified()) {
                    Toast.makeText(getContext(), "Please verify your email address", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(view.getContext(), EditProfileActivity.class);
                    intent.putExtra("username", tvUsername.getText().toString());
                    intent.putExtra("email", tvEmail.getText().toString());
                    startActivity(intent);
                }
            }
        });

        return root;
    }
}