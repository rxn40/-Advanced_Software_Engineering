package com.example.haushaltsapp.DeleteCategoryPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.haushaltsapp.R;
import com.example.haushaltsapp.Database.Category;
import java.util.ArrayList;

public class deleteCategoryAdapter extends RecyclerView.Adapter<deleteCategoryAdapter.MyViewHolderCat> {

    private ArrayList<Category> categoryList;
    private deleteCategoryClickListener listener;

    public deleteCategoryAdapter(ArrayList<Category> categoryList, deleteCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }


    public class MyViewHolderCat extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView catName;

        public MyViewHolderCat(final View view)
        {
            super(view);
            catName = view.findViewById(R.id.CategoryDeleteBox);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public deleteCategoryAdapter.MyViewHolderCat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_category,parent,false);
        return new MyViewHolderCat(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull deleteCategoryAdapter.MyViewHolderCat holder, int position) {
        String nameCat = categoryList.get(position).getName_PK();
        int colorCat = categoryList.get(position).getColor();
        holder.catName.setText(nameCat);
        holder.catName.setTextColor(colorCat);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public interface deleteCategoryClickListener {
        void  onClick(View v, int position);
    }

    public void deleteCategory(int position) {
        categoryList.remove(position);
        notifyItemRemoved(position);
    }


}
