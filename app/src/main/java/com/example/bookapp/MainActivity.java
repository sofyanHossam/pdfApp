package com.example.bookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.bookapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    binding=ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());



    binding.loginBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent (MainActivity.this,LoginActivity.class) );
        }
    });
    binding.gust.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setGuesstName();
        }
    });
    binding.gust.setPaintFlags(binding.gust.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        binding.sinUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (MainActivity.this,RegisterActivity.class) );
            }
        });
    }

    private void setGuesstName()  {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("اسم المستخدم");
        LinearLayout linearLayout=new LinearLayout(this);
         EditText emailEt =new EditText(this);
        emailEt.setHint(" ادخل اسمك ");
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        builder.setPositiveButton("تم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name =emailEt.getText().toString();

                Intent intent =new Intent(MainActivity.this,DashboardUserActivity.class);
                intent.putExtra("ame",name);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create().show();


    }
}