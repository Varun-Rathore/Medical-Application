package com.example.medicalappv3.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.Adapters.MessageAdapter;
import com.example.medicalappv3.Fragments.APIService;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.ChatDataBaseHelper;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.ActivatedUser;
import com.example.medicalappv3.UserDefinedClasses.ChatInfo;
import com.example.medicalappv3.UserDefinedClasses.MessageInfo;
import com.example.medicalappv3.UserDefinedClasses.Notifications.Client;
import com.example.medicalappv3.UserDefinedClasses.Notifications.Data;
import com.example.medicalappv3.UserDefinedClasses.Notifications.MyResponse;
import com.example.medicalappv3.UserDefinedClasses.Notifications.Sender;
import com.example.medicalappv3.UserDefinedClasses.Notifications.Token;
import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageAreaActivity extends AppCompatActivity {
    Toolbar toolbar;
    String revname;
    UserDataBaseHelper mydb;
    ChatDataBaseHelper chatdb = null;
    Cursor userres, chatres;
    DatabaseReference databaseReference;
    EditText typemessage;
    ImageButton sendmessage;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<MessageInfo> messageInfoList;
    APIService apiService;
    boolean notify = false;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_area);
        Intent intent = getIntent();
        revname = intent.getStringExtra("receiver");
        ActivatedUser.setActivated(revname);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(revname);
        setSupportActionBar(toolbar);
        mp = MediaPlayer.create(MessageAreaActivity.this, R.raw.woosh);
        mp.setVolume(1.0f, 1.0f);
        mydb = new UserDataBaseHelper(MessageAreaActivity.this);
        chatdb = new ChatDataBaseHelper(MessageAreaActivity.this);
        userres = mydb.getUser();
        userres.moveToFirst();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        typemessage = findViewById(R.id.typemessage);
        sendmessage = findViewById(R.id.sendmessage);
        messageInfoList = new ArrayList<>();
        recyclerView = findViewById(R.id.messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        readMessage(userres.getString(0), revname);
        if (userres.getString(3).equals("Patient")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("ChatTime")
                                                .child(revname + userres.getString(0));
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("ChatTime")
                                                .child(userres.getString(0) + revname);
        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date1, date2;
                Date date = new Date();
                ChatInfo info = dataSnapshot.getValue(ChatInfo.class);
                try {
                    date1 = formatter.parse(info.getDatecreated());
                    date2 = formatter.parse(formatter.format(date));
                    long difference = Math.abs(date1.getTime() - date2.getTime());
                    long diffdays = 30 - difference / (24 * 60 * 60 * 1000);
                    if (diffdays < 0) {
                        findViewById(R.id.message_box).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.message_box).setVisibility(View.VISIBLE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("MessageArea");
                final MessageInfo messageInfo = new MessageInfo(userres.getString(0), revname,
                                                                typemessage.getText().toString());
                final String msg = typemessage.getText().toString();
                String id = databaseReference1.push().getKey();
                databaseReference1.child(id).setValue(messageInfo);
                chatdb.addMessage(messageInfo, id);
                mp.start();
                displaymessage(revname, userres.getString(0));
                typemessage.setText("");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                                              .child(userres.getString(0));
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData data = dataSnapshot.getValue(UserData.class);
                        if (notify) { sendNotification(revname, userres.getString(0), msg); }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        displaymessage(revname, userres.getString(0));
    }

    private void sendNotification(String receiver, final String username, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(userres.getString(0), msg, "Message from " + username, username);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                              .enqueue(new Callback<MyResponse>() {
                                  @Override
                                  public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                  }

                                  @Override
                                  public void onFailure(Call<MyResponse> call, Throwable t) {

                                  }
                              });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessageAreaActivity.this, "canclled Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void readMessage(final String myid, final String userid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MessageArea");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                displaymessage(userid, myid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void displaymessage(final String userid, final String myid) {
        if (chatdb == null) { return; }
        messageInfoList.clear();
        chatres = chatdb.getMessage(userid, myid);
        while (chatres.moveToNext()) {
            MessageInfo messageInfo = new MessageInfo(chatres.getString(0), chatres.getString(1), chatres.getString(2));
            messageInfoList.add(messageInfo);
        }
        messageAdapter = new MessageAdapter(MessageAreaActivity.this, messageInfoList);
        recyclerView.setAdapter(messageAdapter);
    }

    @Override
    public void onBackPressed() {
        ActivatedUser.setActivated(null);
        super.onBackPressed();
    }
}
