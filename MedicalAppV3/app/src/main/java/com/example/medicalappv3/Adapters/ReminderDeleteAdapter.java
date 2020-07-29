package com.example.medicalappv3.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.R;
import com.example.medicalappv3.UserDefinedClasses.ReminderData;
import com.example.medicalappv3.UserDefinedClasses.todelete;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ReminderDeleteAdapter extends RecyclerView.Adapter<ReminderDeleteAdapter.ViewHolder> {
    private Context context;
    private List<ReminderData> reminderDataList;

    public ReminderDeleteAdapter(Context context, List<ReminderData> reminderDataList) {
        this.context = context;
        this.reminderDataList = reminderDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reminder_safe_delete, parent, false);
        return new ReminderDeleteAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ReminderData reminderData = reminderDataList.get(position);
        holder.medname.setText(reminderData.getMedname());
        long timeinmillis = reminderData.getTimeinmillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeinmillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        holder.timeis.setText(simpleDateFormat.format(calendar.getTime()).toUpperCase());
        final boolean[] days_selected = reminderData.getSelected_days();
        for (int i = 0; i < 7; i++) {
            if (days_selected[i]) {
                holder.buttons[i].setBackgroundResource(R.drawable.button_selected);
            } else {
                holder.buttons[i].setBackgroundResource(R.drawable.button_not_selected);
            }
        }
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    todelete.pos.add(position);
                } else {
                    for (int i = 0; i < todelete.pos.size(); i++) {
                        if (position == todelete.pos.get(i)) {
                            todelete.pos.remove(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView medname;
        public TextView timeis;
        public Button[] buttons;
        public CheckBox check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medname = itemView.findViewById(R.id.med_name);
            timeis = itemView.findViewById(R.id.timeis);
            buttons = new Button[]{itemView.findViewById(R.id.sun),
                                   itemView.findViewById(R.id.mon),
                                   itemView.findViewById(R.id.tue),
                                   itemView.findViewById(R.id.wed),
                                   itemView.findViewById(R.id.thu),
                                   itemView.findViewById(R.id.fri),
                                   itemView.findViewById(R.id.sat)};
            check = itemView.findViewById(R.id.checkbox);
        }
    }
}
