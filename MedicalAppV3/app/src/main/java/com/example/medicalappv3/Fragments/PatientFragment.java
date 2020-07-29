package com.example.medicalappv3.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.medicalappv3.Activities.MainActivity;
import com.example.medicalappv3.Adapters.ViewPagerAdapter;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PatientFragment extends Fragment {

    UserDataBaseHelper mydb;
    String patient;
    Cursor res;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage, container, false);
        mydb = new UserDataBaseHelper(getContext());
        res = mydb.getUser();
        res.moveToFirst();
        patient = res.getString(0);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(new GetPrescriptionFragment(), "Prescriptions");
        viewPagerAdapter.addFragment(new ChatFragment(), "Chats");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        checkChange();
        return view;
    }

    void checkChange() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (isConnected) {
            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(res.getString(7), res.getString(1))
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (!task.isSuccessful()) {
                             UserDataBaseHelper mydb = new UserDataBaseHelper(getContext());
                             mydb.logout();
                             FirebaseAuth.getInstance().signOut();
                             final Context context = getContext();
                             Intent i = new Intent(context, MainActivity.class);
                             startActivity(i);
                             getActivity().finish();
                         }
                     }
                 });
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(patient);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mydb.updateRegular(dataSnapshot.getValue(UserData.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
