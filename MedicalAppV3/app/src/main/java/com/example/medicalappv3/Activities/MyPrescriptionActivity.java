package com.example.medicalappv3.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalappv3.R;
import com.example.medicalappv3.UserDefinedClasses.PrescriptionData;

public class MyPrescriptionActivity extends AppCompatActivity {
    TextView docname, date, data, topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescriptpattern);
        Intent i = getIntent();
        PrescriptionData prescriptionData = (PrescriptionData) i.getSerializableExtra("prescrip");
        docname = findViewById(R.id.docname);
        date = findViewById(R.id.date);
        data = findViewById(R.id.data);
        topic = findViewById(R.id.topic);
        docname.setText(prescriptionData.getDOCTOR_NAME());
        date.setText(prescriptionData.getDATE());
        topic.setText(prescriptionData.getTOPIC());
        data.setVisibility(View.VISIBLE);
        data.setText("\n\n" + prescriptionData.getDATA());
    }
}

