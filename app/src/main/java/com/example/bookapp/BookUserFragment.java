package com.example.bookapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookapp.databinding.FragmentBookUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookUserFragment extends Fragment {


    private String id;
    private String uid;
    private String category;

    private ArrayList<ModelPdf> modelPdfArrayList;
    private AdapterPdfUser adapterPdfUser;

    private FragmentBookUserBinding binding;


    public BookUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment BookUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookUserFragment newInstance(String id, String category, String uid) {
        BookUserFragment fragment = new BookUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", id);
        args.putString("category", category);
        args.putString("categoryUid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("categoryUid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookUserBinding.inflate(LayoutInflater.from(getContext()), container, false);

        if (category.equals("كل الاقسام")){
            loadAll();
        }
        else {
            loadSelectedCategory();
        }

        binding.searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterPdfUser.getFilter().filter(s);
                }
                catch (Exception e)
                {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

            return binding.getRoot();
    }

    private void loadSelectedCategory() {
        modelPdfArrayList=new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(id)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelPdfArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelPdf modelPdf =ds.getValue(ModelPdf.class);
                    modelPdfArrayList.add(modelPdf);
                }
                adapterPdfUser=new AdapterPdfUser(getContext(),modelPdfArrayList);
                binding.categoryRV.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAll() {
        modelPdfArrayList=new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelPdfArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelPdf modelPdf =ds.getValue(ModelPdf.class);
                    modelPdfArrayList.add(modelPdf);
                }
                adapterPdfUser=new AdapterPdfUser(getContext(),modelPdfArrayList);
                binding.categoryRV.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}