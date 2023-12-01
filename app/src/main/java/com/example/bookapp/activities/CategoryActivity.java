package com.example.bookapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.R;
import com.example.bookapp.databinding.ActivityCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityCategoryBinding binding;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("انتظر قليلا");
        progressDialog.setCanceledOnTouchOutside(false);
        
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
    }
private String category;
    private void validateData() {
        category =binding.cateET.getText().toString().trim();
        if (TextUtils.isEmpty(category)){
            Toast.makeText(CategoryActivity.this, " ادخل اسم القسم ", Toast.LENGTH_SHORT).show();
        }
        else {
            addCategory();
        }
    }

    private void addCategory() {
        progressDialog.setMessage("جاري اضافة قسم ");
        progressDialog.show();

        String timeStamp =""+System.currentTimeMillis();
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("uid" , auth.getUid());
        hashMap.put("id" , timeStamp);
        hashMap.put("category" , category);
        hashMap.put("timestamp" , timeStamp);

        FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference reference =database.getReference("Categories");
        reference.child(""+timeStamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(CategoryActivity.this, " تم اضافة قسم جديد ", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}