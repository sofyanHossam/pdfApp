package com.example.bookapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.activities.PdfViewActivity;
import com.example.bookapp.databinding.RowFavoriteBookBinding;
import com.example.bookapp.filters.FilterPdfFav;
import com.example.bookapp.models.ModelPdf;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterFav extends RecyclerView.Adapter<AdapterFav.holderFav> implements Filterable {
    private final Context context;
    public ArrayList<ModelPdf> modelPdfArrayList, filterList;
    private FilterPdfFav filterPdfFav;
    private RowFavoriteBookBinding binding;

    public AdapterFav(Context context, ArrayList<ModelPdf> modelPdfArrayList) {
        this.context = context;
        this.modelPdfArrayList = modelPdfArrayList;
        this.filterList = modelPdfArrayList;

    }

    @NonNull
    @Override
    public holderFav onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowFavoriteBookBinding.inflate(LayoutInflater.from(context),parent ,false);
        return new holderFav(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull holderFav holder, int position) {

        ModelPdf modelPdf=modelPdfArrayList.get(position);
        String bookId=modelPdf.getId();
        loadBookInfo(modelPdf,holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, PdfViewActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);

            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(bookId);
            }
        });



    }

    private void loadSize(ModelPdf modelPdf, AdapterFav.holderFav holder) {
        String pdfUrl=modelPdf.getUrl();
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes=storageMetadata.getSizeBytes();
                        double kb= bytes/1024;
                        double mb= kb/1024;
                        if (mb >=1){
                            holder.size.setText(String.format("%.2f",mb)+" MB");
                        }else if (kb >=1){
                            holder.size.setText(String.format("%.2f",kb)+" KB");
                        }else {
                            holder.size.setText(String.format("%.2f",bytes)+" bytes");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadBookInfo(ModelPdf modelPdf, holderFav holder) {

        String bookId=modelPdf.getId();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Books");
        ref .child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String bookTitle=""+snapshot.child("name").getValue();
                        String bookCategory=""+snapshot.child("category").getValue();
                        String bookDescription=""+snapshot.child("description").getValue();
                        String timestamp=""+snapshot.child("timestamp").getValue();
                        String bookUrl=""+snapshot.child("url").getValue();
                        String uid=""+snapshot.child("uid").getValue();
                        String id=""+snapshot.child("id").getValue();

                        modelPdf.setFavorite(true);
                        modelPdf.setCategory(bookCategory);
                        modelPdf.setName(bookTitle);
                        modelPdf.setDescription(bookDescription);
                        modelPdf.setId(id);
                        modelPdf.setUrl(bookUrl);
                        modelPdf.setTimestamp(timestamp);
                        modelPdf.setUid(uid);

                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(timestamp));
                        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();

                        holder.title.setText(bookTitle);
                        holder.cate.setText(bookCategory);
                        holder.desc.setText(bookDescription);
                        holder.date.setText(date);
                        loadSize(modelPdf,holder);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void    remove(String bookId){

        FirebaseAuth auth=FirebaseAuth.getInstance();

        if (auth.getCurrentUser()==null){
            Toast.makeText(context, "انت غير مشترك", Toast.LENGTH_SHORT).show();
        }else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(auth.getUid()).child("Favorites").child(bookId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "تمت الازالة من المفضلة", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return modelPdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterPdfFav == null) {
            filterPdfFav = new FilterPdfFav(modelPdfArrayList, this);
        }
        return filterPdfFav;
    }

    class holderFav extends RecyclerView.ViewHolder {
        TextView title, desc, size, date, cate;
        ImageView imageView;

        public holderFav(@NonNull View itemView) {
            super(itemView);
            title = binding.title;
            desc = binding.description;
            date = binding.date;
            size = binding.size;
            cate=binding.category;
            imageView=binding.fav;

        }
    }

}
