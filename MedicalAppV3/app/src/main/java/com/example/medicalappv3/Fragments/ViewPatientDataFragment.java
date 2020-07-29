package com.example.medicalappv3.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.medicalappv3.R;
import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ViewPatientDataFragment extends Fragment {

    public static String presuser;
    UserData userData;
    TextView mob, age, gen, name, email;
    Button seemore;
    boolean datashown = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.viewpatientdata, container, false);
        getuserData();
        mob = view.findViewById(R.id.mob);
        age = view.findViewById(R.id.age);
        gen = view.findViewById(R.id.gen);
        name = view.findViewById(R.id.name);
        seemore = view.findViewById(R.id.see_more);
        email = view.findViewById(R.id.email);
        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datashown) {
                    presuser = userData.getUSERNAME();
                    getParentFragmentManager().beginTransaction()
                                              .replace(R.id.fragment_container, new ViewPatientPresFragment()).commit();
                }
            }
        });
        return view;
    }

    void getuserData() {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.customdialog, null);
        final EditText uname, otp;
        uname = view.findViewById(R.id.user);
        otp = view.findViewById(R.id.otp);
        builder.setPositiveButton("GET DATA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                                      .child(uname.getText().toString());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(UserData.class) != null) {
                            if (dataSnapshot.getValue(UserData.class).getOTP().equals(otp.getText().toString())) {
                                if (Calendar.getInstance().getTimeInMillis() - Long.parseLong(dataSnapshot.getValue(UserData.class).getTimeis()) > 300000) {
                                    Toast.makeText(getContext(), "OTP expired", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                userData = dataSnapshot.getValue(UserData.class);
                                showData();
                            } else {
                                Toast.makeText(getContext(), "Otp incorrect", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Username incorrect", Toast.LENGTH_SHORT).show();
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

    void showData() {
        name.setText(userData.getNAME());
        mob.setText(userData.getMOBILE());
        age.setText(userData.getAGE());
        gen.setText(userData.getGENDER());
        email.setText(userData.getEMAIL());
        datashown = true;
    }
}
