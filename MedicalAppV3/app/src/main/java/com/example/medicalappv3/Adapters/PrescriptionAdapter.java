package com.example.medicalappv3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.Activities.MyPrescriptionActivity;
import com.example.medicalappv3.R;
import com.example.medicalappv3.UserDefinedClasses.PrescriptionData;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {
    private Context context;
    private List<PrescriptionData> prescriptionDataList;


    public PrescriptionAdapter(Context context, List<PrescriptionData> prescriptionDataList) {
        this.context = context;
        this.prescriptionDataList = prescriptionDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.prescriptpattern, parent, false);
        return new PrescriptionAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final PrescriptionData prescriptionData = prescriptionDataList.get(position);
        holder.docname.setText(prescriptionData.getDOCTOR_NAME());
        holder.date.setText(prescriptionData.getDATE());
        holder.topic.setText(prescriptionData.getTOPIC());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MyPrescriptionActivity.class);
                i.putExtra("prescrip", prescriptionData);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return prescriptionDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView docname;
        public TextView date;
        public TextView topic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            docname = itemView.findViewById(R.id.docname);
            date = itemView.findViewById(R.id.date);
            topic = itemView.findViewById(R.id.topic);
        }
    }
}
