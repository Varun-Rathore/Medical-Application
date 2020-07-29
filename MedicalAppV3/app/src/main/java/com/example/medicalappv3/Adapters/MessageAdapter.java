package com.example.medicalappv3.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.MessageInfo;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<MessageInfo> messageInfoList;

    public MessageAdapter(Context context, List<MessageInfo> infos) {
        this.context = context;
        this.messageInfoList = infos;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
        } else { view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false); }
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageInfo messageInfo = messageInfoList.get(position);
        holder.show_message.setText(messageInfo.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageInfoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        UserDataBaseHelper mydb = new UserDataBaseHelper(context);
        Cursor res = mydb.getUser();
        res.moveToFirst();
        if (messageInfoList.get(position).getSender().equals(res.getString(0))) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
        }
    }
}