package com.example.medicalappv3.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    Spinner gen,pat_doc;
    Button submit;
    EditText uname, pass, fname, lname, age, mobile, email;
    ImageButton setvisible;
    UserDataBaseHelper mydb;
    boolean isvisible = false;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mydb = new UserDataBaseHelper(this);
        submit = findViewById(R.id.submit);
        progressBar = findViewById(R.id.pgbar);
        pat_doc = findViewById(R.id.pat_doc);
        uname = findViewById(R.id.uname);
        pass = findViewById(R.id.pass);
        gen = findViewById(R.id.gen);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        mobile = findViewById(R.id.mobile);
        age = findViewById(R.id.age);
        email = findViewById(R.id.email);
        setvisible = findViewById(R.id.set_visible);
        progressBar.setVisibility(View.GONE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.Gender, android.R.layout.simple_dropdown_item_1line);
        gen.setAdapter(adapter);
        adapter = ArrayAdapter
                .createFromResource(this, R.array.Occupation, android.R.layout.simple_dropdown_item_1line);
        pat_doc.setAdapter(adapter);
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullname = fname.getText().toString() + " " + lname.getText().toString();
                final String occu;
                occu = pat_doc.getSelectedItem().toString();
                clearFocus();
                if (email.getText().toString().equals("")) {
                    email.requestFocus();
                    Toast.makeText(getApplicationContext(), "Email required", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email.getText().toString())) {
                    email.requestFocus();
                    Toast.makeText(getApplicationContext(), "Email Address is not valid", Toast.LENGTH_SHORT).show();
                } else if (uname.getText().toString().equals("")) {
                    uname.requestFocus();
                    Toast.makeText(getApplicationContext(), "Username required", Toast.LENGTH_SHORT).show();
                } else if (pass.getText().toString().equals("")) {
                    pass.requestFocus();
                    Toast.makeText(getApplicationContext(), "Password required", Toast.LENGTH_SHORT).show();
                } else if (pass.getText().toString().length() < 6) {
                    pass.requestFocus();
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters",
                                   Toast.LENGTH_SHORT).show();
                } else if (fname.getText().toString().equals("")) {
                    fname.requestFocus();
                    Toast.makeText(getApplicationContext(), "First name required", Toast.LENGTH_SHORT).show();
                } else if (mobile.getText().toString().equals("")) {
                    mobile.requestFocus();
                    Toast.makeText(getApplicationContext(), "Mobile Number required", Toast.LENGTH_SHORT).show();
                } else if (age.getText().toString().equals("")) {
                    age.requestFocus();
                    Toast.makeText(getApplicationContext(), "Age required", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                                                .child(uname.getText().toString());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(UserData.class) != null) {
                                if (Objects.equals(dataSnapshot.getValue(UserData.class).getUSERNAME(),
                                                   uname.getText().toString())) {
                                    Toast.makeText(SignUpActivity.this, "UserName Exists", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    uname.requestFocus();
                                    return;
                                }
                            }
                            final FirebaseAuth mAuth;
                            mAuth = FirebaseAuth.getInstance();
                            mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                     @Override
                                     public void onComplete(@NonNull Task<AuthResult> task) {
                                         if (task.isSuccessful()) {
                                             // Sign in success, update UI with the signed-in user's information
                                             mydb.addUser(uname.getText().toString(),
                                                          pass.getText().toString(),
                                                          fullname, occu,
                                                          mobile.getText().toString(),
                                                          age.getText().toString(),
                                                          gen.getSelectedItem().toString(), email.getText().toString());
                                             final Context context = getApplicationContext();
                                             Cursor res = mydb.getUser();
                                             res.moveToFirst();
                                             res.moveToFirst();
                                             Intent i = new Intent(context, Homepage_DrawerActivity.class);
                                             startActivity(i);
                                             SignUpActivity.this.finish();
                                         } else {
                                             email.requestFocus();
                                             Toast.makeText(SignUpActivity.this, "Email Exists",
                                                            Toast.LENGTH_SHORT).show();
                                             progressBar.setVisibility(View.GONE);
                                         }
                                     }
                                 });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    void clearFocus() {
        email.clearFocus();
        uname.clearFocus();
        pass.clearFocus();
        fname.clearFocus();
        mobile.clearFocus();
        age.clearFocus();
    }

    boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
