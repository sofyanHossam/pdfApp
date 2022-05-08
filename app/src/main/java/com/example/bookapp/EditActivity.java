package com.example.bookapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookapp.databinding.ActivityEditBinding;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ActivityEditBinding binding;
    private Uri image_uri=null;
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_REQUEST_PICK_CAMERA_CODE=300;
    private static final int IMAGE_REQUEST_PICK_GALLERY_CODE=400;

    ProgressDialog pd ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        pd= new ProgressDialog(this);
        pd.setTitle("انتظر...");
        pd.setCanceledOnTouchOutside(false);


        binding=ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadMyInfo();
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

private String name="";
    private void validateData() {
        name=binding.UNameTv.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "ادخل الاسم ", Toast.LENGTH_SHORT).show();
        }
        else {
            updateProfile();
        }
    }

    private void updateProfile() {
        pd.setMessage("Updating...");
        pd.show();
        if (image_uri==null)
        {
            HashMap<String ,Object> hashMap=new HashMap<>();

            hashMap.put("name" , name);

            FirebaseDatabase database =FirebaseDatabase.getInstance();

            DatabaseReference reference =database.getReference("Users");
            reference.child(auth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(EditActivity.this, "تم تغير البيانات ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(EditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            String filePathAndName ="Profile_images/"+""+auth.getUid();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri =uriTask.getResult();

                            if (uriTask.isSuccessful())
                            {
                                HashMap<String ,Object> hashMap=new HashMap<>();

                                hashMap.put("name" , name);

                                hashMap.put("Profile Img" , ""+downloadImageUri);

                                FirebaseDatabase database =FirebaseDatabase.getInstance();

                                DatabaseReference reference =database.getReference("Users");
                                reference.child(auth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(EditActivity.this, "updated Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(EditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(EditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showImagePickDialog() {
        PopupMenu popupMenu =new PopupMenu(this,binding.profileImage);
        popupMenu.getMenu().add(Menu.NONE,0,0,"كاميرا");
        popupMenu.getMenu().add(Menu.NONE,1,1,"المعرض");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int which =item.getItemId();
                if (which==0){
                   pickImageCam();
                }
                else if(which==1)
                {
                    pickImageGallery();
                }
                return false;
            }
        });
    }

    private void pickImageGallery() {
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galAct.launch(intent);
    }

    private void pickImageCam() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New pick");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Image Description");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        camAct.launch(intent);
    }

    private ActivityResultLauncher<Intent> camAct =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode()==Activity.RESULT_OK){
                        Intent data =result.getData();
                        binding.profileImage.setImageURI(image_uri);
                    }else {
                        Toast.makeText(EditActivity.this, "تم الالغاء", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> galAct =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {


                    if (result.getResultCode()==Activity.RESULT_OK){
                        Intent data =result.getData();
                        image_uri =data.getData();
                        binding.profileImage.setImageURI(image_uri);
                    }else {
                        Toast.makeText(EditActivity.this, "تم الالغاء", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

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

                        binding.UNameTv.setText(name);
//
//                                Glide.with(EditActivity.this)
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