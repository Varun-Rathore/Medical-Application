package com.example.medicalappv3.Adapters;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.R;
import com.example.medicalappv3.UserDefinedClasses.Notifications.Reminder_Notification_receiver;
import com.example.medicalappv3.UserDefinedClasses.ReminderData;
import com.example.medicalappv3.UserDefinedClasses.ReminderDataProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private Context context;
    private List<ReminderData> reminderDataList;
    private ReminderDataProvider reminderDataProvider;

    public ReminderAdapter(Context context, List<ReminderData> reminderDataList) {
        this.context = context;
        this.reminderDataList = reminderDataList;
        reminderDataProvider = new ReminderDataProvider(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reminder_layout, parent, false);
        return new ReminderAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ReminderData[] reminderData = {reminderDataList.get(position)};
        reminderDataProvider.StoreReminder(reminderData[0]);
        setNotification(position);
        holder.medname.setText(reminderData[0].getMedname());
        long timeinmillis = reminderData[0].getTimeinmillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeinmillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        holder.timeis.setText(simpleDateFormat.format(calendar.getTime()).toUpperCase());
        final boolean[] days_selected = reminderData[0].getSelected_days();
        for (int i = 0; i < 7; i++) {
            if (days_selected[i]) {
                holder.buttons[i].setBackgroundResource(R.drawable.button_selected);
            } else {
                holder.buttons[i].setBackgroundResource(R.drawable.button_not_selected);
            }
        }
        holder.medname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setName(holder, position);
            }
        });
        holder.timeis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(holder, position);
            }
        });
        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            holder.buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (days_selected[finalI]) {
                        holder.buttons[finalI].setBackgroundResource(R.drawable.button_not_selected);
                        days_selected[finalI] = false;
                    } else {
                        holder.buttons[finalI].setBackgroundResource(R.drawable.button_selected);
                        days_selected[finalI] = true;
                    }
                    reminderDataList.get(position).setSelected_days(days_selected);
                    setNotification(position);
                }
            });
        }
    }

    private void setTime(@NonNull final ViewHolder holder, final int position) {
        final Calendar calendar = Calendar.getInstance();
        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
                holder.timeis.setText(simpleDateFormat.format(calendar.getTime()).toUpperCase());
                reminderDataList.get(position).setTimeinmillis(calendar.getTimeInMillis());
                setNotification(position);

            }
        };
        new TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void setNotification(final int position) {
        long timeinmillis = reminderDataList.get(position).getTimeinmillis();
        Intent intent = new Intent(context, Reminder_Notification_receiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("medname", reminderDataList.get(position).getMedname());
        bundle.putBooleanArray("days", reminderDataList.get(position).getSelected_days());
        bundle.putInt("position", position);
        bundle.putLong("timeinmilli", timeinmillis);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeinmillis, AlarmManager.INTERVAL_DAY, pendingIntent);
        updateRemiderclass();
        //Toast.makeText(context, "Notification set", Toast.LENGTH_SHORT).show();
    }

    void updateRemiderclass() {
        reminderDataProvider.emptyDb();
        for (int i = 0; i < getItemCount(); i++) {
            reminderDataProvider.StoreReminder(reminderDataList.get(i));
        }
    }

    private void setName(@NonNull final ViewHolder holder, final int position) {
        final ReminderData prev = reminderDataList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.getname, null);
        final EditText gettext = view.findViewById(R.id.getmedname);
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!gettext.getText().toString().equals("")) {
                    holder.medname.setText(gettext.getText().toString());
                    reminderDataList.get(position).setMedname(gettext.getText().toString());
                    setNotification(position);
                    reminderDataProvider.updateReminder(reminderDataList.get(position), prev);
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setView(view);
        builder.setTitle("Medicine Name");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return reminderDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView medname;
        public TextView timeis;
        public Button[] buttons;

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
        }
    }
}
