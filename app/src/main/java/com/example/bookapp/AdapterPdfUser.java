package com.example.bookapp;

import android.app.ProgressDialog;
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

import com.example.bookapp.databinding.RowPdfAdminBinding;
import com.example.bookapp.databinding.RowpdfuserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.holderPdfUser> implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    private FilterPdfUser filterPdf;
    private RowpdfuserBinding binding;

    public AdapterPdfUser(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public holderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding =RowpdfuserBinding.inflate(LayoutInflater.from(context),parent ,false);
        return new holderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull holderPdfUser holder, int position) {
        ModelPdf modelPdf=pdfArrayList.get(position);
        String title =modelPdf.name;
        String category =modelPdf.category;
        String description =modelPdf.description;
        String timestamp =modelPdf.timestamp;

        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.title.setText(title);
        holder.desc.setText(description);
        holder.date.setText(date);
        loadSize(modelPdf,holder);
        loadCategory(modelPdf,holder);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookId=modelPdf.getId();

                Intent intent =new Intent(context,PdfViewActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });

    }

    private void loadCategory(ModelPdf modelPdf, holderPdfUser holder) {
        String categoryId=modelPdf.getCategoryId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category=""+snapshot.child("category").getValue();
                        holder.cate.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadSize(ModelPdf modelPdf, holderPdfUser holder) {
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

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterPdf==null)
        {
            filterPdf=new FilterPdfUser(filterList,this);
        }
        return filterPdf;
    }


    class holderPdfUser extends RecyclerView.ViewHolder{
        TextView title,desc,size,date,cate;
        ImageView more;
        public holderPdfUser(@NonNull View itemView) {
            super(itemView);
            title=binding.title;
            desc=binding.description;
            date=binding.date;
            size=binding.size;
            cate=binding.category;
            //more =binding.nextIV;

        }
    }

}
