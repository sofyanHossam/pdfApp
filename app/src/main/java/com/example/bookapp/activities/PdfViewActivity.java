package com.example.bookapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.bookapp.Constants;
import com.example.bookapp.R;
import com.example.bookapp.databinding.ActivityPdfViewBinding;
import com.example.bookapp.models.ModelPdf;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class PdfViewActivity extends AppCompatActivity {
    String bookId;
    String bookPrice;
    private FirebaseAuth auth;
    private boolean isFav;
    private ActivityPdfViewBinding binding;
    private String score = "" + 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_pdf_view);
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        auth = FirebaseAuth.getInstance();
        ///////
        binding = ActivityPdfViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref
                .child(auth.getUid())
                .child("Favorites")
                .child(bookId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isFav = snapshot.exists();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Books");
        ref1.child(bookId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookPrice = "" + snapshot.child("price").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        score = "" + snapshot.child("score").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        loadBookDetails();
        if (auth.getCurrentUser() != null) {
            checkFav();
        }


        // loadBookDetails();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        binding.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    removeFavorite();
                } else {
                    addToFavorite();
                    //////
                }
            }
        });

    }


    private void checkFav() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref
                .child(auth.getUid())
                .child("Favorites")
                .child(bookId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (isFav) {
                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);
                            binding.fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_baseline_star_24, 0, 0);
                            binding.addText.setText("ازالة من المفضلة");
                            modelPdf.setFavorite(true);
                        } else {
                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);
                            binding.fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_baseline_non_star_24, 0, 0);
                            binding.addText.setText("اضافة الي المفضلة");
                            modelPdf.setFavorite(false);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void addToFavorite() {
        if (Integer.valueOf(score) > Integer.valueOf(bookPrice)) {
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "انت غير مشترك", Toast.LENGTH_SHORT).show();
            } else {
                if (isFav) {
                    Toast.makeText(this, "تم الاضافة من قبل ", Toast.LENGTH_SHORT).show();
                } else {
                    String timestamp = "" + System.currentTimeMillis();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("bookId", bookId);
                    hashMap.put("timestamp", timestamp);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(auth.getUid()).child("Favorites").child(bookId)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(PdfViewActivity.this, "تمت الاضافة الي المفضلة", Toast.LENGTH_SHORT).show();
                                    checkFav();
                                    loadBookDetails();
                                    int newScore = Integer.valueOf(score) - Integer.valueOf(bookPrice);

                                    ////
                                    ////
                                    ////

                                    {
                                        HashMap<String, Object> hashMap = new HashMap<>();

                                        hashMap.put("score", newScore);
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = database.getReference("Users");
                                        reference.child(auth.getUid()).updateChildren(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(PdfViewActivity.this, "رصيدك الان : " + newScore, Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        Toast.makeText(PdfViewActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PdfViewActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        } else {
            Toast.makeText(this, "لا يوجد رصيد كاف !", Toast.LENGTH_SHORT).show();
        }


    }

    private void removeFavorite() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "انت غير مشترك", Toast.LENGTH_SHORT).show();
        } else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(auth.getUid()).child("Favorites").child(bookId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PdfViewActivity.this, "تمت الازالة من المفضلة", Toast.LENGTH_SHORT).show();
                            checkFav();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PdfViewActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void loadBookDetails() {
        binding.pb.setVisibility(View.VISIBLE);

        DatabaseReference referenceBook = FirebaseDatabase.getInstance().getReference("Books");
        referenceBook.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String pdfUrl = "" + snapshot.child("url").getValue();

                        loadBookfromUrl(pdfUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBookfromUrl(String pdfUrl) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        if (isFav) {
            ref.getBytes(Constants.MAX_BYTES_PDF)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            binding.pdfViewer.fromBytes(bytes)
                                    .swipeHorizontal(false)
                                    .fitEachPage(true)
                                    .onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {
                                            int cPage = (page + 1);
                                            binding.bookName.setText(cPage + " /" + pageCount);
                                            binding.price.setText(bookPrice + "جنيه");
                                        }
                                    }).onError(new OnErrorListener() {
                                        @Override
                                        public void onError(Throwable t) {
                                            Toast.makeText(PdfViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).nightMode(false)

                                    .load();
                            binding.pb.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.pb.setVisibility(View.GONE);
                            //Toast.makeText(PdfViewActivity.this, "ملف كبير للغالية", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            ref.getBytes(Constants.MAX_BYTES_PDF)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            binding.pdfViewer.fromBytes(bytes)
                                    .enableSwipe(false)
                                    .onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {
                                            int cPage = (page + 1);
                                            binding.bookName.setText(cPage + " /" + pageCount);
                                            binding.price.setText(bookPrice + "جنيه");
                                        }
                                    }).onError(new OnErrorListener() {
                                        @Override
                                        public void onError(Throwable t) {
                                            Toast.makeText(PdfViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).nightMode(false)
                                    .load();
                            binding.pb.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.pb.setVisibility(View.GONE);
                            //Toast.makeText(PdfViewActivity.this, "ملف كبير للغالية", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

}