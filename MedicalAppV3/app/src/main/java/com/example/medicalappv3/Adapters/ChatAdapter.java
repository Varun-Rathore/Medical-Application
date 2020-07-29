package com.example.medicalappv3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.Activities.MessageAreaActivity;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.ChatDataBaseHelper;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.ActivatedUser;
import com.example.medicalappv3.UserDefinedClasses.ChatInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<ChatInfo> infos;

    public ChatAdapter(Context context, List<ChatInfo> infos) {
        this.context = context;
        this.infos = infos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_mainpage_row, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ChatInfo chatInfo = infos.get(position);
        final String[] generatedName = {""};
        UserDataBaseHelper mydb = new UserDataBaseHelper(context);
        final Cursor res = mydb.getUser();
        res.moveToFirst();
        if (res.getString(3).equals("Doctor")) {
            generatedName[0] = chatInfo.getPatuname();
        } else {
            generatedName[0] = chatInfo.getDocuname();
        }
        holder.name.setText(generatedName[0]);
        final ChatDataBaseHelper chatdb = new ChatDataBaseHelper(context);
        final Cursor[] chatres = {chatdb.getMessage(generatedName[0], res.getString(0))};
        if (chatres[0].getCount() != 0) {
            chatres[0].moveToLast();
            holder.last_message.setText(chatres[0].getString(2));
        }
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date1.setTimeInMillis(Long.parseLong(chatInfo.getDatecreated()));
        long difference = Math.abs(date1.getTimeInMillis() - date2.getTimeInMillis());
        long diffdays = 30 - difference / (24 * 60 * 60 * 1000);
        String daydiff = "";
        if (diffdays >= 0) {
            daydiff = Long.toString(diffdays) + " days left";
        }
        holder.day_left.setText(daydiff);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MessageArea");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatres[0] = chatdb.getMessage(generatedName[0], res.getString(0));
                if (chatres[0].getCount() != 0) {
                    chatres[0].moveToLast();
                    holder.last_message.setText(chatres[0].getString(2));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageAreaActivity.class);
                intent.putExtra("receiver", generatedName[0]);
                ActivatedUser.setActivated(generatedName[0]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView day_left;
        public TextView last_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            day_left = itemView.findViewById(R.id.days_left);
            last_message = itemView.findViewById(R.id.last_text);
        }
    }
}
