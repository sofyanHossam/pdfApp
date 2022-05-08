package com.example.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bookapp.databinding.ActivityAddPdfBinding;
import com.example.bookapp.databinding.ActivityPdfEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class pdfEditActivity extends AppCompatActivity {
    private ActivityPdfEditBinding binding;
    private FirebaseAuth auth;
    private String bookId;
    private ProgressDialog progressDialog;
    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_edit);
        binding =ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bookId=getIntent().getStringExtra("bookId");

        loadCategory();
        loadBookInfo();
        binding.addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        binding.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("انتظر قليلا");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private String name , desc1;
    private void validateData () {

        name = binding.title.getText().toString().trim();
        desc1=binding.descProduct.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, " ادخل الاسم...", Toast.LENGTH_SHORT).show();
        }


        else  if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, " اختر القسم...", Toast.LENGTH_SHORT).show();
        }
        else {
            update();
        }
    }

    private void update() {

        progressDialog.setMessage("جااري تعديل الملف");
        progressDialog.show();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("name",""+name);
        hashMap.put("description",""+desc1);
        hashMap.put("categoryId",""+selectedCategoryId);

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Books");
        reference.child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(pdfEditActivity.this, "تم التعديل", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(pdfEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void loadBookInfo() {
        DatabaseReference referenceBook = FirebaseDatabase.getInstance().getReference("Books");
        referenceBook.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        selectedCategoryId =""+snapshot.child("categoryId").getValue();
                        String title =""+snapshot.child("name").getValue();
                        String description =""+snapshot.child("description").getValue();
                        binding.title.setText(title);
                        binding.descProduct.setText(description);
                        DatabaseReference referenceBookC = FirebaseDatabase.getInstance().getReference("Categories");
                        referenceBookC.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String category =""+snapshot.child("category").getValue();
                                        binding.category.setText(category);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private String selectedCategoryId,selectedCategoryTitle;
    private void categoryDialog(){
        String [] categoryArray =new String[categoryTitleArrayList.size()];
        for (int i =0;i<categoryTitleArrayList.size();i++ ){
            categoryArray[i]=categoryTitleArrayList.get(i);
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("choose category");
        builder.setItems(categoryArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategoryId=categoryIdArrayList.get(which);
                selectedCategoryTitle=categoryTitleArrayList.get(which);

                binding.category.setText(selectedCategoryTitle);
            }
        }).show();
    }
    private void loadCategory(){

        categoryTitleArrayList= new ArrayList<>();
        categoryIdArrayList= new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for(DataSnapshot ds :snapshot.getChildren()) {
                    String categoryId =""+ds.child("id").getValue();
                    String categoryTitle =""+ds.child("category").getValue();
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}