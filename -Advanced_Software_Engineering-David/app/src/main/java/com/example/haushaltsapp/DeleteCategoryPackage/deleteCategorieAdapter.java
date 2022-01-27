package com.example.haushaltsapp.DeleteCategoryPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.haushaltsapp.R;
import com.example.haushaltsapp.database.Category;
import java.util.ArrayList;

public class deleteCategorieAdapter extends RecyclerView.Adapter<deleteCategorieAdapter.MyViewHolderCat> {

    private ArrayList<Category> categoryList;
    private deleteCategorieClickListener listener;

    public deleteCategorieAdapter(ArrayList<Category> categoryList, deleteCategorieClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }


    public class MyViewHolderCat extends  RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView catname;

        public MyViewHolderCat(final View view)
        {
            super(view);
            catname = view.findViewById(R.id.CategorieDeleteBox);
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
        String nameCat = categoryList.get(position).getName_PK();
        int colorCat = categoryList.get(position).getColor();
        holder.catname.setText(nameCat);
        holder.catname.setTextColor(colorCat);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public interface deleteCategorieClickListener{
        void  onClick(View v, int position);
    }

    public void deleteCategorie (int position) {
        categoryList.remove(position);
        notifyItemRemoved(position);
    }


}
