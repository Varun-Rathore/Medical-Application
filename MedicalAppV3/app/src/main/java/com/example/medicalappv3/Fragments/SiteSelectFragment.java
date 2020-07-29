package com.example.medicalappv3.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv3.Adapters.SiteLayoutAdapter;
import com.example.medicalappv3.R;

public class SiteSelectFragment extends Fragment {

    public static RecyclerView recyclerView;
    String[] name;
    Integer[] id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_select, container, false);
        name = new String[]{"1mg", "PharmEasy", "NetMeds", "MedPlus Mart"};
        id = new Integer[]{R.drawable.site1, R.drawable.site2, R.drawable.site3, R.drawable.site4};
        recyclerView = view.findViewById(R.id.sel_site);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SiteLayoutAdapter adapter = new SiteLayoutAdapter(getParentFragmentManager(), getContext(), name, id);
        recyclerView.setAdapter(adapter);

        return view;


    }

}