package com.example.medicalappv3.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.Adapters.PrescriptionAdapter;
import com.example.medicalappv3.R;
import com.example.medicalappv3.UserDefinedClasses.PrescriptionData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewPatientPresFragment extends Fragment {
    ListView list;
    List<PrescriptionData> prescriptionDataList;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.viewpatientpres, container, false);
        final String username = ViewPatientDataFragment.presuser;
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        prescriptionDataList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PRESCRIPTIONS");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prescriptionDataList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    PrescriptionData prescriptionData = userSnapshot.getValue(PrescriptionData.class);
                    if (prescriptionData.getPATIENT_ID().equals(username)) {
                        prescriptionDataList.add(prescriptionData);
                    }
                }
                PrescriptionAdapter adapter = new PrescriptionAdapter(getContext(), prescriptionDataList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
}
