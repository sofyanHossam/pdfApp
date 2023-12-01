package com.example.bookapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.R;
import com.example.bookapp.adapters.AdapterCategory;
import com.example.bookapp.databinding.ActivityDashoardAdminBinding;
import com.example.bookapp.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashoardAdminActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityDashoardAdminBinding binding;
    public ArrayList<ModelCategory> categoryArrayList;
    public AdapterCategory adapterCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashoard_admin);
        binding = ActivityDashoardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();

binding.add.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(DashoardAdminActivity.this, AddPdfActivity.class));
    }
});
binding.profile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(DashoardAdminActivity.this,ProfileActivity.class));
    }
});
        checkUser();
        loadCategories();
        binding.searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterCategory.getFilter().filter(s);
                }
                catch (Exception e)
                {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashoardAdminActivity.this,CategoryActivity.class));
            }
        });
        binding.Ulogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                checkUser();
            }
        });
    }

    private void loadCategories() {
        categoryArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoryArrayList.clear();
                        for(DataSnapshot ds :snapshot.getChildren()) {
                            ModelCategory modelProduct = ds.getValue(ModelCategory.class);
                            categoryArrayList.add(modelProduct);
                        }
                        adapterCategory=new AdapterCategory(DashoardAdminActivity.this,categoryArrayList);
                        binding.categoryRV.setAdapter(adapterCategory);
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    private void checkUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(auth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name =""+snapshot.child("name").getValue();
                            binding.UNameTv.setText(""+name);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            startActivity(new Intent(DashoardAdminActivity.this, MainActivity.class));
            finish();
        }
    }


}