package com.example.medicalappv3.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.medicalappv3.Activities.MainActivity;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountFragment extends Fragment {

    Button save, logout;
    String user;
    UserDataBaseHelper mydb;
    EditText mob, age, pass, email;
    TextView temail, tmob, tage, tpass, tgen;
    RadioGroup radioGroup;
    RadioButton genm, genf;
    FirebaseUser firebaseUser;
    ImageButton setvisible, tsetvisible;
    boolean isvisible = false, tisvisible = false, editmode = false;
    View view;
    String hidepat = "*************";
    TableLayout layout_view, layout_edit;
    Cursor res;
    UserData myUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.account, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mydb = new UserDataBaseHelper(getContext());
        res = mydb.getUser();
        res.moveToFirst();
        checkChange();
        user = res.getString(0);
        mob = view.findViewById(R.id.mob);
        age = view.findViewById(R.id.age);
        pass = view.findViewById(R.id.pass);
        radioGroup = view.findViewById(R.id.gen);
        genm = view.findViewById(R.id.male);
        genf = view.findViewById(R.id.female);
        save = view.findViewById(R.id.save);
        email = view.findViewById(R.id.email);
        logout = view.findViewById(R.id.logout);
        tmob = view.findViewById(R.id.tmob);
        tage = view.findViewById(R.id.tage);
        tpass = view.findViewById(R.id.tpass);
        temail = view.findViewById(R.id.temail);
        tgen = view.findViewById(R.id.tgen);
        layout_view = view.findViewById(R.id.layout_view);
        layout_edit = view.findViewById(R.id.layout_edit);
        setvisible = view.findViewById(R.id.set_visible);
        tsetvisible = view.findViewById(R.id.tset_visible);
        viewData();
        tsetvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tisvisible) {
                    tsetvisible.setImageResource(R.drawable.not_visible);
                    tisvisible = false;
                    tpass.setText(hidepat);
                } else {
                    tsetvisible.setImageResource(R.drawable.visible);
                    tisvisible = true;
                    tpass.setText(res.getString(1));
                }
            }
        });
        setvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isvisible) {
                    setvisible.setImageResource(R.drawable.not_visible);
                    isvisible = false;
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    setvisible.setImageResource(R.drawable.visible);
                    isvisible = true;
                    pass.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                pass.setSelection(pass.getText().length());
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editmode) {
                    updateData();

                } else {
                    editmode = true;
                    editData();
                    layout_view.setVisibility(View.GONE);
                    layout_edit.setVisibility(View.VISIBLE);
                    save.setText("SAVE");
                    save.setBackgroundColor(getResources().getColor(R.color.sushi));
                    logout.setVisibility(View.GONE);
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.logout();
                final Context context = getContext();
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        return view;
    }

    void checkChange() {
        final Context context = getContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (!isConnected) {
            return;
        }
        Cursor res = mydb.getUser();
        res.moveToFirst();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(res.getString(7), res.getString(1))
             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if (!task.isSuccessful()) {
                         UserDataBaseHelper mydb = new UserDataBaseHelper(context);
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

    void viewData() {

        tmob.setText(res.getString(4));
        tage.setText(res.getString(5));
        tpass.setText(hidepat);
        temail.setText(res.getString(7));
        tgen.setText(res.getString(6));
    }

    void editData() {
        mob.setText(res.getString(4));
        age.setText(res.getString(5));
        if (res.getString(6).equals("MALE")) {
            genm.setChecked(true);
        } else {
            genf.setChecked(true);
        }
        email.setText(res.getString(7));
        pass.setText(res.getString(1));
    }

    void updateData() {
        Context context = getContext();
        if (email.getText().toString().equals("")) {
            email.requestFocus();
            Toast.makeText(context, "Email can't be empty", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(email.getText().toString())) {
            email.requestFocus();
            Toast.makeText(context, "new Email Address is not valid", Toast.LENGTH_SHORT).show();
        } else if (mob.getText().toString().equals("")) {
            pass.requestFocus();
            Toast.makeText(context, "Mobile Number can't be empty", Toast.LENGTH_SHORT).show();
        } else if (age.getText().toString().equals("")) {
            pass.requestFocus();
            Toast.makeText(context, "Age can't be empty", Toast.LENGTH_SHORT).show();
        } else if (pass.getText().toString().equals("")) {
            pass.requestFocus();
            Toast.makeText(context, "Password can't be empty", Toast.LENGTH_SHORT).show();
        } else if (pass.getText().toString().length() < 6) {
            pass.requestFocus();
            Toast.makeText(context, "new Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
            if (!isConnected) {
                Toast.makeText(context, "Network connection is required to Change values", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = radioGroup.getCheckedRadioButtonId();
            RadioButton rb = view.findViewById(id);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (!pass.getText().toString().equals(res.getString(1))) {
                firebaseUser.updatePassword(pass.getText().toString());
            }
            if (!email.getText().toString().equals(res.getString(1))) {
                firebaseUser.updateEmail(email.getText().toString());
            }
            mydb.updateData(user, pass.getText().toString(), mob.getText().toString(), age.getText().toString(),
                            rb.getText().toString(), email.getText().toString());
            res = mydb.getUser();
            res.moveToFirst();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                                  .child(res.getString(0));
            UserData userData = new UserData(res.getString(0),
                                             null,
                                             res.getString(2),
                                             res.getString(3),
                                             res.getString(4),
                                             res.getString(5),
                                             res.getString(6),
                                             res.getString(7));
            databaseReference.setValue(userData);
            Toast.makeText(context, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
            editmode = false;
            viewData();
            layout_view.setVisibility(View.VISIBLE);
            layout_edit.setVisibility(View.GONE);
            save.setText("CHANGE");
            save.setBackgroundColor(getResources().getColor(R.color.silver_chalice));
            logout.setVisibility(View.VISIBLE);
        }
    }

    boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
