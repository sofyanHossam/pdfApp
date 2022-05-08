package com.example.bookapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.databinding.RowCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyHolder> implements Filterable {
    private Context context;
    private RowCategoryBinding binding;
    private FilterCategory filterCategory;
    public AdapterCategory(Context context, ArrayList<ModelCategory> categoriesArray) {
        this.context = context;
        this.categoriesArray = categoriesArray;
        this.filterList = categoriesArray;
    }

    public ArrayList<ModelCategory> categoriesArray,filterList ;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false);

        return new MyHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
ModelCategory modelCategory =categoriesArray.get(position);
String id =modelCategory.getId();
String uid =modelCategory.getUid();
String category =modelCategory.getCategory();
String timestamp =modelCategory.getTimestamp();


holder.categoryTV.setText(category);
holder.delIV.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v)  {

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("حذف")
                .setMessage("حذف قسم  "+category+"؟")
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "جاري الحذف", Toast.LENGTH_SHORT).show();
                        delete(modelCategory,holder);
                    }
                }).setNegativeButton("تراجع", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })

                .show();
    }
});
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent= new Intent(context, pdfListAdminActivity.class);
        intent.putExtra("categoryId",id);
        intent.putExtra("categoryTitle",category);
        context.startActivity(intent);
    }
});
    }

    private void delete(ModelCategory modelCategory, MyHolder holder){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        String id =modelCategory.getId();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "تم حذف القسم...", Toast.LENGTH_SHORT).show();
                        
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
        return categoriesArray.size();
    }

    @Override
    public Filter getFilter() {
        if (filterCategory==null)
        {
            filterCategory=new FilterCategory(filterList,this);
        }
        return filterCategory;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView categoryTV;
        ImageView delIV;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            categoryTV=binding.cateTitle;
            delIV=binding.delteIV;


        }
    }

}

