package com.example.haushaltsapp.DeleteCategoryPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haushaltsapp.ChartPackage.RecyclerAdapter;
import com.example.haushaltsapp.DeleteCategoryActivity;
import com.example.haushaltsapp.R;
import com.example.haushaltsapp.database.Category;
import com.example.haushaltsapp.database.MySQLite;

import java.util.ArrayList;

public class deleteCategorieAdapter extends RecyclerView.Adapter<deleteCategorieAdapter.MyViewHolderCat> {

    private ArrayList<Category> CategoryList;
    private deleteCategorieClickListener listener;

    public deleteCategorieAdapter(ArrayList<Category> CategoryList, deleteCategorieClickListener listener)
    {
        this.CategoryList = CategoryList;
        this.listener = listener;
    }

    public class MyViewHolderCat extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView Catname;
        CheckBox deleteCat;

        public MyViewHolderCat(final View view)
        {
            super(view);
            Catname = view.findViewById(R.id.CategoryDeleteCheckBox);
            //deleteCat = view.findViewById(R.id.CategoryDeleteCheckBox);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public deleteCategorieAdapter.MyViewHolderCat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_category,parent,false);
        return new MyViewHolderCat(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull deleteCategorieAdapter.MyViewHolderCat holder, int position) {

        String nameCat = CategoryList.get(position).getName_PK();
        holder.Catname.setText(nameCat);

    }

    @Override
    public int getItemCount() {
        return CategoryList.size();
    }

    public  interface deleteCategorieClickListener{

        void  onClick(View v, int position);

    }

    public void deleteCategorie (int position) {
        CategoryList.remove(position);
        notifyItemRemoved(position);
    }


}
