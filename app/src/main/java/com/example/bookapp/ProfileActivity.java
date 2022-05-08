package com.example.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bookapp.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private ArrayList<ModelPdf> pdfArrayList;
    private List<ModelPdf>pdfList;
    private AdapterFav adapterFav;
//   public PdfDB pdfDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        binding =ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        auth=FirebaseAuth.getInstance();
      //  pdfDB=PdfDB.getInstance(ProfileActivity.this);
        loadMyInfo();
        loadFavBook();

        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,EditActivity.class));
            }
        });
    }

    private void loadFavBook() {
        pdfArrayList =new ArrayList<>();


//        Favorites
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Users");
        reference.child(auth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        pdfArrayList.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            String bookId =""+ds.child("bookId").getValue();
                            ModelPdf modelPdf =new ModelPdf();
                            modelPdf.setId(bookId);
                            pdfArrayList.add(modelPdf);

                        }
                        adapterFav=new AdapterFav(ProfileActivity.this,pdfArrayList);
                        binding.recF.setAdapter(adapterFav);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String name =""+snapshot.child("name").getValue();
                            String email =""+snapshot.child("email").getValue();
                            String timestamp =""+snapshot.child("timestamp").getValue();
                            String uid =""+snapshot.child("uid").getValue();
                            String ProfileImg =""+snapshot.child("Profile Img").getValue();
                            String accType =""+snapshot.child("accountType").getValue();
                            String password =""+snapshot.child("password").getValue();
                        binding.email.setText(email);
                        binding.UNameTv.setText(name);
//
//                        Glide.with(ProfileActivity.this)
//                                .load(ProfileImg)
//                                .placeholder(R.drawable.ic_baseline_person_24)
//                                .into(binding.profileImage);

                        try {
                            Picasso.get().load(ProfileImg).placeholder(R.drawable.ic_baseline_person_24).into(binding.profileImage);
                        }catch (Exception e)
                        {
                            binding.profileImage.setImageResource(R.drawable.ic_baseline_person_24);
                        }

                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}