package com.example.medicalappv3.UserDefinedClasses;

public class ChatInfo {
    String docuname;
    String patuname;
    String datecreated;

    public ChatInfo(String docuname, String patuname, String datecreated) {
        this.docuname = docuname;
        this.patuname = patuname;
        this.datecreated = datecreated;
    }

    public ChatInfo() {
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public String getDocuname() {
        return docuname;
    }

    public void setDocuname(String docuname) {
        this.docuname = docuname;
    }

    public String getPatuname() {
        return patuname;
    }

    public void setPatuname(String patuname) {
        this.patuname = patuname;
    }
}
