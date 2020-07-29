package com.example.medicalappv3.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

public class OTPFragment extends Fragment {
    UserDataBaseHelper mydb;
    TextView otp, description;
    Cursor res;
    UserData userData;
    DatabaseReference databaseReference;
    boolean isConnected;
    Button gen_new;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.otp, container, false);
        description = rootview.findViewById(R.id.description);
        otp = rootview.findViewById(R.id.otp);
        otp.setText("");
        gen_new = rootview.findViewById(R.id.gen_new);
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (!isConnected) {
            description.setText("Network connection is required to Send OTP");
            otp.setText("");
            gen_new.setVisibility(View.GONE);
            return rootview;
        }
        description.setText("Generate Otp");
        mydb = new UserDataBaseHelper(getContext());
        res = mydb.getUser();
        res.moveToFirst();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(res.getString(0));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                Calendar calendar = Calendar.getInstance();
                if (data.getOTP() != null) {
                    long timestamp = Long.parseLong(data.getTimeis());
                    if (calendar.getTimeInMillis() - timestamp < 300000) {
                        otp.setText(data.getOTP());
                        description.setText("Share this otp with doctor");
                        gen_new.setVisibility(View.GONE);
                    } else {
                        delvalue();
                        otp.setText("");
                        description.setText("Generate Otp");
                        gen_new.setVisibility(View.VISIBLE);
                    }
                } else {
                    otp.setText("");
                    description.setText("Generate Otp");
                    gen_new.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        gen_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                isConnected = activeNetwork != null && activeNetwork.isConnected();
                if (!isConnected) {
                    description.setText("Network connection is required to Send OTP");
                    otp.setText("");
                } else {
                    userData = new UserData(res.getString(0),
                                            null,
                                            res.getString(2),
                                            res.getString(3),
                                            res.getString(4),
                                            res.getString(5),
                                            res.getString(6),
                                            res.getString(7));
                    Random random = new Random();
                    final Integer randomnumber = random.nextInt(999999 - 100000) + 100000;
                    userData.setOTP(randomnumber.toString());
                    userData.setTimeis(Long.toString(Calendar.getInstance().getTimeInMillis()));
                    databaseReference.setValue(userData);
                    description.setText("Share this otp with Doctor");
                    otp.setText(randomnumber.toString());
                    gen_new.setVisibility(View.GONE);

                }
            }
        });
        return rootview;
    }

    public void delvalue() {
        userData = new UserData(res.getString(0),
                                null,
                                res.getString(2),
                                res.getString(3),
                                res.getString(4),
                                res.getString(5),
                                res.getString(6),
                                res.getString(7));
        userData.setOTP(null);
        userData.setTimeis(null);
        databaseReference.setValue(userData);
    }
}
