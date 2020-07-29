package com.example.medicalappv3.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.Adapters.ReminderAdapter;
import com.example.medicalappv3.Adapters.ReminderDeleteAdapter;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.ShowCheckHelper;
import com.example.medicalappv3.UserDefinedClasses.Notifications.Reminder_Notification_receiver;
import com.example.medicalappv3.UserDefinedClasses.ReminderData;
import com.example.medicalappv3.UserDefinedClasses.ReminderDataProvider;
import com.example.medicalappv3.UserDefinedClasses.todelete;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class ReminderFragment extends Fragment {
    public static Menu menu;
    Context context;
    RecyclerView recyclerView;
    List<ReminderData> reminderDataList;
    ReminderDataProvider provider;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        View rootview = inflater.inflate(R.layout.fragment_reminder, container, false);
        provider = new ReminderDataProvider(context);
        reminderDataList = new ArrayList<>();
        reminderDataList = provider.getAllReminders();
        recyclerView = rootview.findViewById(R.id.remind);
        fab = rootview.findViewById(R.id.fab);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ReminderAdapter adapter = new ReminderAdapter(context, reminderDataList);
        recyclerView.setAdapter(adapter);
        ShowDialog(container);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fab.getTag().toString().equals("add")) {
                    ReminderData reminderData = new ReminderData();
                    provider.StoreReminder(reminderData);
                    reminderDataList.clear();
                    reminderDataList = provider.getAllReminders();
                    ReminderAdapter adapter = new ReminderAdapter(context, reminderDataList);
                    recyclerView.setAdapter(adapter);
                } else if (fab.getTag().toString().equals("delete")) {
                    deletedata();
                    fab.setTag("add");
                    fab.setImageResource(R.drawable.ic_add);
                }

            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (fab.getTag().toString().equals("add")) {
                    ReminderDeleteAdapter adapter = new ReminderDeleteAdapter(context, reminderDataList);
                    recyclerView.setAdapter(adapter);
                    fab.setImageResource(R.drawable.ic_delete);
                    fab.setTag("delete");
                } else {
                    ReminderAdapter adapter = new ReminderAdapter(context, reminderDataList);
                    recyclerView.setAdapter(adapter);
                    fab.setImageResource(R.drawable.ic_add);
                    fab.setTag("add");

                }
                return true;

            }
        });
        return rootview;
    }

    void ShowDialog(View view) {
        final ShowCheckHelper helper = new ShowCheckHelper(getActivity());
        Cursor hell = helper.getcheck();
        while (hell.moveToNext()) {
            if (hell.getString(0).equals("2")) {
                return;
            }
        }
        Snackbar.make(view, "To delete reminders long click on plus icon", BaseTransientBottomBar.LENGTH_LONG).setAction("Don't Show Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.addcheck("2");
            }
        }).show();

    }

    private void deletedata() {
        clearAllNotification();
        List<ReminderData> dataList = new ArrayList<>();
        boolean[] flag = new boolean[reminderDataList.size()];
        for (int i = 0; i < reminderDataList.size(); i++) {
            flag[i] = true;
        }
        for (int i = 0; i < todelete.pos.size(); i++) {
            int toremove = todelete.pos.get(i);
            flag[toremove] = false;
        }
        for (int i = 0; i < reminderDataList.size(); i++) {
            if (flag[i]) {
                dataList.add(reminderDataList.get(i));
            }
        }
        reminderDataList.clear();
        reminderDataList = dataList;
        todelete.pos.clear();
        updateRemiderclass();
        ReminderAdapter adapter = new ReminderAdapter(context, reminderDataList);
        recyclerView.setAdapter(adapter);
    }

    void updateRemiderclass() {
        provider.emptyDb();
        for (int i = 0; i < reminderDataList.size(); i++) {
            provider.StoreReminder(reminderDataList.get(i));
        }
    }

    private void clearAllNotification() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Intent intent = new Intent(context, Reminder_Notification_receiver.class);
        for (int i = 0; i < reminderDataList.size(); i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void onStop() {
        provider.close();
        super.onStop();
    }
}