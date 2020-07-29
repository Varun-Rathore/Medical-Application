package com.example.medicalappv3.UserDefinedClasses;

import java.io.Serializable;

public class PrescriptionData implements Serializable {
    String DOCTOR_NAME;
    String DATE;
    String DATA;
    String PATIENT_ID;
    String TOPIC;

    public PrescriptionData(String DOCTOR_NAME, String DATE, String DATA, String PATIENT_ID, String TOPIC) {
        this.DOCTOR_NAME = DOCTOR_NAME;
        this.DATE = DATE;
        this.DATA = DATA;
        this.PATIENT_ID = PATIENT_ID;
        this.TOPIC = TOPIC;
    }

    public PrescriptionData() {}

    public String getDOCTOR_NAME() {
        return DOCTOR_NAME;
    }

    public void setDOCTOR_NAME(String DOCTOR_NAME) {
        this.DOCTOR_NAME = DOCTOR_NAME;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
    }

    public String getPATIENT_ID() {
        return PATIENT_ID;
    }

    public void setPATIENT_ID(String PATIENT_ID) {
        this.PATIENT_ID = PATIENT_ID;
    }

    public String getTOPIC() {
        return TOPIC;
    }

    public void setTOPIC(String TOPIC) {
        this.TOPIC = TOPIC;
    }
}
