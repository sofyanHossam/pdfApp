package com.example.bookapp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.bookapp.databinding.ActivityMainBinding;
import com.example.bookapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
   private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        progressDialog.setTitle("انتظر قليلا");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.haveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });
    }
   private String Name, email, pass, Cpass;

    private void validateData()
    {
        Name = binding.LNameEt.getText().toString().trim();
        email = binding.LEmailEt.getText().toString().trim();
        pass = binding.LPasswordEt.getText().toString().trim();
        Cpass = binding.cPasswordEt.getText().toString().trim();
       

        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(this, "ادخل الاسم...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, " ادخل البريد الالكتروني...", Toast.LENGTH_SHORT).show();
        }
       
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "بريد الكتروني غير موجود...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "ادخل كلمة السر...", Toast.LENGTH_SHORT).show();

        } else if (pass.length() < 6) {
            Toast.makeText(this, "كلمة المرور يجب ان تكون 6 حروف اوكثر...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(Cpass)) {
            Toast.makeText(this, "ادخل كلمة السر مرة اخرى...", Toast.LENGTH_SHORT).show();

        } else if (!pass.equals(Cpass)) {
            Toast.makeText(this, "كلمة المرور والتأكيد غير متماثلين", Toast.LENGTH_SHORT).show();
        }

        else {
            createAcc();
        }
    }

    private void createAcc() {
progressDialog.setTitle("جاري انشاء حساب......");
progressDialog.show();

        auth.createUserWithEmailAndPassword(email,pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFirebaseData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("جاري حفظ البيانات ....");
        String timeStamp =""+System.currentTimeMillis();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("email" , email);
        hashMap.put("password" , pass);
        hashMap.put("uid" , auth.getUid());
        hashMap.put("name" , Name);
        hashMap.put("timestamp" , timeStamp);
        hashMap.put("accountType" , "user");
        hashMap.put("PDF URI" , "");

        FirebaseDatabase database =FirebaseDatabase.getInstance();

        DatabaseReference reference =database.getReference("Users");
        reference.child(auth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, " تم انشاء حساب جديد", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this , DashboardUserActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}