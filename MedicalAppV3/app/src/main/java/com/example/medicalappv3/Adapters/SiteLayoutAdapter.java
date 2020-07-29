package com.example.medicalappv3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.R;
import com.example.medicalappv3.UserDefinedClasses.WebPos;
import com.example.medicalappv3.WebFragment;


public class SiteLayoutAdapter extends RecyclerView.Adapter<SiteLayoutAdapter.ViewHolder> {
    private Context context;
    private String[] name;
    private Integer[] id;
    private FragmentManager fragmentManager;

    public SiteLayoutAdapter(FragmentManager fragmentManager, Context context, String[] name, Integer[] id) {
        this.context = context;
        this.name = name;
        this.id = id;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.site_layout, parent, false);
        return new SiteLayoutAdapter.ViewHolder(view);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textView.setText(name[position]);
        holder.imageView.setImageResource(id[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebPos.position = position;
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                                           new WebFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.site_image);
            textView = itemView.findViewById(R.id.med_name);
        }
    }
}
