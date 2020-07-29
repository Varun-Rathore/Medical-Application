package com.example.medicalappv3.UserDefinedClasses;

import java.util.Calendar;

public class ReminderData {
    String medname;
    long timeinmillis;
    boolean[] selected_days;

    public ReminderData(String medname, long timeinmillis, boolean[] selected_days) {
        this.medname = medname;
        this.timeinmillis = timeinmillis;
        this.selected_days = selected_days;
    }

    public ReminderData() {
        this.medname = "Medicine name";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.timeinmillis = calendar.getTimeInMillis();
        this.selected_days = new boolean[]{true, true, true, true, true, true, true};
    }

    public String getMedname() {
        return medname;
    }

    public void setMedname(String medname) {
        this.medname = medname;
    }

    public long getTimeinmillis() {
        return timeinmillis;
    }

    public void setTimeinmillis(long timeinmillis) {
        this.timeinmillis = timeinmillis;
    }

    public boolean[] getSelected_days() {
        return selected_days;
    }

    public void setSelected_days(boolean[] selected_days) {
        this.selected_days = selected_days;
    }
}
