package com.example.bookapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.databinding.ActivityPdfEditBinding;
import com.example.bookapp.databinding.RowPdfAdminBinding;
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

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.holderPdfAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    private RowPdfAdminBinding binding;
    private FilterPdf filterPdf;
    private ProgressDialog progressDialog;

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("انتظر...");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @NonNull
    @Override
    public holderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding =RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent ,false);
        return new holderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull holderPdfAdmin holder, int position) {

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookId=modelPdf.getId();
                String bookUrl =modelPdf.getUrl();
                String bookTitle =modelPdf.getName();

                Intent intent =new Intent(context,PdfViewActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionDialog(modelPdf,holder);
            }
        });


        loadSize(modelPdf,holder);
       loadCategory(modelPdf,holder);

    }

    private void moreOptionDialog(ModelPdf modelPdf, holderPdfAdmin holder) {
        String bookId=modelPdf.getId();


        String [] option ={"تعديل","حذف"};
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(" اختر")
                .setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){

                            Intent intent=new Intent(context, pdfEditActivity.class);
                            intent.putExtra("bookId",bookId);
                            //intent.putExtra("bookTitle",bookTitle);
                           // intent.putExtra("bookUrl",bookUrl);
                            context.startActivity(intent);

                        }else if (which==1){
                            deleteBooks(modelPdf,holder);
                        }
                    }
                }).show();
    }

    private void deleteBooks(ModelPdf modelPdf, holderPdfAdmin holder) {

        String bookId=modelPdf.getId();
        String bookUrl =modelPdf.getUrl();
        String bookTitle =modelPdf.getName();

        progressDialog.setMessage("جاري حذف "+bookTitle);
        progressDialog.show();

        StorageReference storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(bookId).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "تم حذف الكتاب...", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadSize(ModelPdf modelPdf, holderPdfAdmin holder) {
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

    private void loadCategory(ModelPdf modelPdf, holderPdfAdmin holder) {
        String categoryId=modelPdf.getCategoryId();
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Categories");
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

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterPdf==null)
        {
            filterPdf=new FilterPdf(filterList,this);
        }
        return filterPdf;
    }

    class holderPdfAdmin extends RecyclerView.ViewHolder{
    TextView title,desc,size,date,cate;
    ImageView more;
        public holderPdfAdmin(@NonNull View itemView) {
            super(itemView);
            title=binding.title;
            desc=binding.description;
            date=binding.date;
            size=binding.size;
            cate=binding.category;
            more =binding.nextIV;
        }
    }
    }
