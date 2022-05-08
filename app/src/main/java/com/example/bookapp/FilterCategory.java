package com.example.bookapp;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    private ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategory adapterCategory;

    public FilterCategory(ArrayList<ModelCategory> categoryArrayList, AdapterCategory adapterCategory) {
        this.categoryArrayList = categoryArrayList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if (constraint!=null&&constraint.length()>0)
        {
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels=new ArrayList<>();
            for( int i=0;i<categoryArrayList.size();i++)
            {
                if (categoryArrayList.get(i).getCategory().toUpperCase().contains(constraint)
                   )
                {
                    filteredModels.add(categoryArrayList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else {
            results.count=categoryArrayList.size();
            results.values=categoryArrayList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterCategory.categoriesArray=(ArrayList<ModelCategory>)results.values;
        adapterCategory.notifyDataSetChanged();
    }
}
