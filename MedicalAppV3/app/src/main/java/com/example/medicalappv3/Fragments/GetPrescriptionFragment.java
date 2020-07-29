package com.example.medicalappv3.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.Adapters.PrescriptionAdapter;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.PrescriptionDataBaseHelper;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.PrescriptionData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GetPrescriptionFragment extends Fragment {
    List<PrescriptionData> prescriptionDataList;
    String username;
    UserDataBaseHelper mydb;
    PrescriptionDataBaseHelper pdb;
    TextView default_text;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_get_prescription, container, false);
        mydb = new UserDataBaseHelper(getContext());
        pdb = new PrescriptionDataBaseHelper(getContext());
        Cursor res = mydb.getUser();
        res.moveToFirst();
        username = res.getString(0);
        recyclerView = rootView.findViewById(R.id.list);
        default_text = rootView.findViewById(R.id.default_text);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        prescriptionDataList = new ArrayList<>();
        getPrescription();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PRESCRIPTIONS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    PrescriptionData prescriptionData = userSnapshot.getValue(PrescriptionData.class);
                    if (prescriptionData.getPATIENT_ID().equals(username)) {
                        pdb.addPrescription(prescriptionData, userSnapshot.getKey());
                    }
                }
                getPrescription();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return rootView;
    }

    void getPrescription() {
        Cursor res = pdb.getPrescription(username);
        if (res.getCount() == 0) { return; }
        default_text.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        prescriptionDataList.clear();
        while (res.moveToNext()) {
            PrescriptionData prescriptionData = new PrescriptionData(res.getString(0), res.getString(1),
                                                                     res.getString(2), res.getString(3),
                                                                     res.getString(4));
            prescriptionDataList.add(prescriptionData);
        }
        PrescriptionAdapter adapter = new PrescriptionAdapter(getContext(), prescriptionDataList);
        recyclerView.setAdapter(adapter);
    }

}
