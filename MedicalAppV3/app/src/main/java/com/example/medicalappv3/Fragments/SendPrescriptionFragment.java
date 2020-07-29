package com.example.medicalappv3.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.ChatInfo;
import com.example.medicalappv3.UserDefinedClasses.PrescriptionData;
import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class SendPrescriptionFragment extends Fragment {
    View view;
    Button sendButton;
    Context context;
    UserDataBaseHelper mydb;
    EditText topic, patpres;
    Cursor res;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_send_prescription, container, false);
        context = getContext();
        mydb = new UserDataBaseHelper(context);
        res = mydb.getUser();
        res.moveToFirst();
        topic = view.findViewById(R.id.topic);
        patpres = view.findViewById(R.id.patpres);
        sendButton = view.findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topic.getText().toString().equals("")) {
                    topic.requestFocus();
                    Toast.makeText(context, "Topic Required", Toast.LENGTH_SHORT).show();
                } else if (patpres.getText().toString().equals("")) {
                    patpres.requestFocus();
                    Toast.makeText(context, "Prescription Required", Toast.LENGTH_SHORT).show();
                } else {
                    sendprestopat();
                }
            }
        });

        return view;
    }

    void sendprestopat() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (!isConnected) {
            Toast.makeText(context, "Network connection is required to Send Data", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.customdialog, null);
        final EditText uname, otp;
        uname = view.findViewById(R.id.user);
        otp = view.findViewById(R.id.otp);
        builder.setPositiveButton("SEND PRESCRIPTION", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                                            .child(uname.getText().toString());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(UserData.class).getOTP() == null) {
                            Toast.makeText(context, "Username or Otp not found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (Objects.requireNonNull(dataSnapshot.getValue(UserData.class).getOTP()).equals(otp.getText().toString())) {
                            if (Calendar.getInstance().getTimeInMillis() - Long.parseLong(dataSnapshot.getValue(UserData.class).getTimeis()) > 300000) {
                                Toast.makeText(context, "OTP expired", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            res = mydb.getUser();
                            res.moveToFirst();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            Date date = new Date();
                            PrescriptionData patdata = new PrescriptionData(res.getString(2), formatter.format(date),
                                                                            patpres.getText().toString(),
                                                                            uname.getText().toString(),
                                                                            topic.getText().toString());
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance()
                                                                                   .getReference("PRESCRIPTIONS");
                            String id = databaseReference1.push().getKey();
                            databaseReference1.child(id).setValue(patdata);
                            activateChat(res.getString(0), uname.getText().toString());
                            Toast.makeText(context, "Prescription Send Successfully", Toast.LENGTH_SHORT).show();
                            topic.setText("");
                            patpres.setText("");
                        } else {
                            Toast.makeText(context, "Otp incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void activateChat(final String docuname, final String patuname) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatTime")
                                                                    .child(docuname + patuname);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatInfo info = dataSnapshot.getValue(ChatInfo.class);
                Calendar calendar = Calendar.getInstance();
                if (info == null) {
                    info = new ChatInfo(docuname, patuname, Long.toString(calendar.getTimeInMillis()));
                } else {
                    info.setDatecreated(Long.toString(calendar.getTimeInMillis()));
                }
                databaseReference.setValue(info);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}