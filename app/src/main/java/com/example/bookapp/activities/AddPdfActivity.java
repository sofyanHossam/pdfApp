package com.example.bookapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.R;
import com.example.bookapp.databinding.ActivityAddPdfBinding;
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

public class AddPdfActivity extends AppCompatActivity {
    private ActivityAddPdfBinding binding;
    private FirebaseAuth auth;
    private static final int PDF_PICK_CODE =1000;
    private Uri pdf_uri= null;
    private static final String TAG="ADD_PDF_TAG";
    private ArrayList<String> categoryTitleArrayList ,categoryIdArrayList;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdf);
        binding =ActivityAddPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
loadCategory();
        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("انتظر قليلا");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.attech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfPick();
            }
        });
        binding.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDialog();
            }
        });
        binding.addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private String name, desc, price;

    private void validateData() {
        Log.d(TAG, "validate");
        name = binding.title.getText().toString().trim();
        desc = binding.descProduct.getText().toString().trim();
        price = binding.price.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, " ادخل اسم المادة...", Toast.LENGTH_SHORT).show();
        } else if (pdf_uri == null) {
            Toast.makeText(this, "لم يتم اختيار ملف...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "ادخل اسم الملف...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "ادخل سعر الملف...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, " اختر القسم...", Toast.LENGTH_SHORT).show();
        } else {
            add();
        }
    }


    private void add() {
        Log.d(TAG,"upload");
        progressDialog.setMessage("جااري تحميل الملف");
        progressDialog.show();
String timestamp=""+ System.currentTimeMillis();
        String filePath="Books/"+timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
        storageReference.putFile(pdf_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"uploaded");
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadpdfuri =""+uriTask.getResult();
                        uploadtoDB(uploadpdfuri,timestamp);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG,"failed upload");
                Toast.makeText(AddPdfActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadtoDB(String uploadpdfuri, String timestamp) {
        Log.d(TAG,"upload to DB");
        progressDialog.setMessage("تحميل بيانات الملف");
        String uid= auth.getUid();
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("categoryId" , selectedCategoryId);
        hashMap.put("category" , selectedCategoryTitle);
        hashMap.put("uid" , uid);
        hashMap.put("name", name);
        hashMap.put("price", price);
        hashMap.put("description", desc);
        hashMap.put("timestamp" , timestamp);
        hashMap.put("id" , timestamp);
        hashMap.put("url" , ""+uploadpdfuri);


        FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference reference =database.getReference("Books");

        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Log.d(TAG,"success upload to DB");
                        Toast.makeText(AddPdfActivity.this, "تم تحميل الملف"+pdf_uri
                                , Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG,"failed upload to DB");
                Toast.makeText(AddPdfActivity.this, ""+e.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String selectedCategoryId,selectedCategoryTitle;
    private void pickDialog() {
        Log.d(TAG,"showing category");
        String[] categoryArray =new String[categoryTitleArrayList.size()];
        for (int i =0 ;i<categoryTitleArrayList.size();i++)
        {
            categoryArray[i]=categoryTitleArrayList.get(i);
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("اختر قسم ").setItems(categoryArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategoryTitle =categoryTitleArrayList.get(which);
                selectedCategoryId =categoryIdArrayList.get(which);
                 binding.category.setText(selectedCategoryTitle);
                Log.d(TAG,"selected category"+selectedCategoryTitle+selectedCategoryId);
            }
        }).show();
    }
    private void loadCategory(){
        Log.d(TAG,"category ...");
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

    private void pdfPick() {
        Log.d(TAG,"pick intent");
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF"),PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==PDF_PICK_CODE){
                Log.d(TAG ,"picked");
                pdf_uri=data.getData();
                Log.d(TAG,"uri"+pdf_uri);
            }

        }else{
            Log.d(TAG,"cancel");
            Toast.makeText(this, "فشل تحمييل الملف", Toast.LENGTH_SHORT).show();
        }
    }
}