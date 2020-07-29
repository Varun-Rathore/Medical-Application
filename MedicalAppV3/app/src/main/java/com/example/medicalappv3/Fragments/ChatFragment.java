package com.example.medicalappv3.Fragments;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.Adapters.ChatAdapter;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.ChatDataBaseHelper;
import com.example.medicalappv3.SQLiteDatabases.ChatInfoDataBaseHelper;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.ActivatedUser;
import com.example.medicalappv3.UserDefinedClasses.ChatInfo;
import com.example.medicalappv3.UserDefinedClasses.MessageInfo;
import com.example.medicalappv3.UserDefinedClasses.Notifications.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    static int vari = 1;
    List<ChatInfo> chatInfoList;
    UserDataBaseHelper mydb;
    ChatInfoDataBaseHelper chdb;
    Cursor res;
    TextView default_text;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        default_text = view.findViewById(R.id.default_text);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatInfoList = new ArrayList<>();
        ActivatedUser.setActivated(null);
        mydb = new UserDataBaseHelper(getContext());
        chdb = new ChatInfoDataBaseHelper(getContext());
        res = mydb.getUser();
        res.moveToFirst();
        displaymembers();
        readUsers();
        readMessages();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void updateToken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(res.getString(0)).setValue(token1);
    }

    void readUsers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatTime");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatInfo chatInfo = snapshot.getValue(ChatInfo.class);
                    if (chatInfo.getPatuname().equals(res.getString(0)) || chatInfo.getDocuname()
                                                                                   .equals(res.getString(0))) {
                        chdb.addInfo(chatInfo, snapshot.getKey());
                    }
                    displaymembers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void displaymembers() {
        Cursor result = chdb.getInfo(res.getString(0));
        if (result.getCount() == 0) { return; }
        default_text.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        chatInfoList.clear();
        while (result.moveToNext()) {
            ChatInfo chatInfo = new ChatInfo(result.getString(0), result.getString(1), result.getString(2));
            chatInfoList.add(chatInfo);
        }
        ChatAdapter adapter = new ChatAdapter(getContext(), chatInfoList);
        recyclerView.setAdapter(adapter);
    }

    private void readMessages() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MessageArea");
        final ChatDataBaseHelper chatdb = new ChatDataBaseHelper(getContext());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageInfo messageInfo = snapshot.getValue(MessageInfo.class);
                    if (messageInfo.getReceiver().equals(res.getString(0))) {
                        chatdb.addMessage(messageInfo, snapshot.getKey());
                        DatabaseReference delreference = FirebaseDatabase.getInstance().getReference("MessageArea")
                                                                         .child(snapshot.getKey());
                        delreference.removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
