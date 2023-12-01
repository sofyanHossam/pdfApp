package com.example.bookapp.filters;

import android.widget.Filter;

import com.example.bookapp.adapters.AdapterPdfAdmin;
import com.example.bookapp.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdf extends Filter {
    private final ArrayList<ModelPdf> modelPdfArrayList;
    private final AdapterPdfAdmin adapterPdfAdmin;

    public FilterPdf(ArrayList<ModelPdf> modelPdfArrayList, AdapterPdfAdmin adapterPdfAdmin) {
        this.modelPdfArrayList = modelPdfArrayList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0)
        {
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filteredModels=new ArrayList<>();
            for( int i=0;i<modelPdfArrayList.size();i++)
            {
                if (modelPdfArrayList.get(i).getName().toUpperCase().contains(constraint)
                )
                {
                    filteredModels.add(modelPdfArrayList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else {
            results.count=modelPdfArrayList.size();
            results.values=modelPdfArrayList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterPdfAdmin.pdfArrayList=(ArrayList<ModelPdf>)results.values;
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
