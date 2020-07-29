package com.example.medicalappv3.UserDefinedClasses;

import android.content.Context;

import com.db4o.ObjectSet;

import java.util.ArrayList;

public class ReminderDataProvider extends Db4oHelper {
    public final static String TAG = "ReminderDataProvider";
    private static ReminderDataProvider provider = null;

    public ReminderDataProvider(Context ctx) { super(ctx); }

    public static ReminderDataProvider getInstance(Context ctx) {
        if (provider == null) { provider = new ReminderDataProvider(ctx); }
        return provider;
    }

    public void StoreReminder(ReminderData con) {

        db().store(con);

        db().commit();

    }

    public void emptyDb() {

        ObjectSet result = db().queryByExample(new Object());

        while (result.hasNext()) {

            db().delete(result.next());

        }
    }

    public ReminderData GetReminderByName(String name) {

        // define

        ReminderData obj = new ReminderData(name, 0, null);

        obj.setMedname(name);

        ObjectSet result = db().queryByExample(obj);


        if (result.hasNext()) {

            return (ReminderData) result.next();

        }

        return null;

    }

    public ReminderData getReminder(ReminderData con) {

        ObjectSet result = db().queryByExample(con);


        if (result.hasNext()) {

            return (ReminderData) result.next();

        } else { return null; }

    }


    public ArrayList<ReminderData> getAllReminders() {


        ArrayList<ReminderData> list = new ArrayList<ReminderData>();

        ReminderData proto = new ReminderData(null, 0, null);

        ObjectSet<ReminderData> result = db().queryByExample(proto);


        while (result.hasNext()) {

            list.add(result.next());

        }

        return list;

    }

    public boolean updateReminder(ReminderData ObjTo, ReminderData ObjFrom) {


        if (deleteReminder(ObjFrom)) {
            StoreReminder(ObjTo);
            return true;
        }
        return false;

    }


    public boolean deleteReminder(ReminderData p) {

        ReminderData found = null;
        ObjectSet<ReminderData> result = db().queryByExample(p);

        if (result.hasNext()) {

            found = result.next();
            db().delete(found);

            return true;

        } else {

            return false;

        }

    }
}