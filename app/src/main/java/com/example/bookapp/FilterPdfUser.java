package com.example.bookapp;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {
    private ArrayList<ModelPdf> modelPdfArrayList;
    private AdapterPdfUser adapterPdfUser;

    public FilterPdfUser(ArrayList<ModelPdf> modelPdfArrayList, AdapterPdfUser adapterPdfUser) {
        this.modelPdfArrayList = modelPdfArrayList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if (constraint!=null||constraint.length()>0)
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
        adapterPdfUser.pdfArrayList=(ArrayList<ModelPdf>)results.values;
        adapterPdfUser.notifyDataSetChanged();
    }
}
