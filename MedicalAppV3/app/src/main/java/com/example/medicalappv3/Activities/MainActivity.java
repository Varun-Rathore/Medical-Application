package com.example.medicalappv3.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.ActivatedUser;
import com.example.medicalappv3.UserDefinedClasses.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    UserDataBaseHelper mydb;
    Button login, signup;
    ImageButton setvisible;
    boolean isvisible = false;
    ProgressBar progressBar;
    TextView fgpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydb = new UserDataBaseHelper(this);
        ActivatedUser activatedUser = new ActivatedUser();
        ActivatedUser.setActivated(null);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Cursor res = mydb.getUser();
            if (res.getCount() != 0) {
                res.moveToFirst();
                Intent i = new Intent(getApplicationContext(), Homepage_DrawerActivity.class);
                startActivity(i);
                MainActivity.this.finish();
            }
        }
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.user);
        password = findViewById(R.id.pass);
        fgpass = findViewById(R.id.fgpass);
        progressBar = findViewById(R.id.pgbar);
        progressBar.setVisibility(View.GONE);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.singup);
        mydb = new UserDataBaseHelper(this);
        setvisible = findViewById(R.id.set_visible);
        setvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isvisible) {
                    setvisible.setImageResource(R.drawable.not_visible);
                    isvisible = false;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    setvisible.setImageResource(R.drawable.visible);
                    isvisible = true;
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                password.setSelection(password.getText().length());
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.clearFocus();
                password.clearFocus();
                ConnectivityManager cm = (ConnectivityManager) MainActivity.this
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
                if (username.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Username/Email required", Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Password required", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                } else if (!isConnected) {
                    Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (isEmail(username.getText().toString())) {
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if (task.isSuccessful()) {
                                         // Sign in success, update UI with the signed-in user's information
                                         DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                                                                               .getReference("Users");
                                         databaseReference.addValueEventListener(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                 for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                     UserData userData = userSnapshot.getValue(UserData.class);
                                                     if (userData.getEMAIL().equals(username.getText().toString())) {
                                                         userData.setPASSWORD(password.getText().toString());
                                                         mydb.addUser(userData);
                                                         break;
                                                     }
                                                 }
                                                 Cursor res = mydb.getUser();
                                                 res.moveToFirst();
                                                 final Context context = getApplicationContext();
                                                 res.moveToFirst();
                                                 Intent i = new Intent(context, Homepage_DrawerActivity.class);
                                                 startActivity(i);
                                                 MainActivity.this.finish();
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {
                                             }
                                         });
                                     } else {
                                         // If sign in fails, display a message to the user.
                                         progressBar.setVisibility(View.GONE);
                                         Toast.makeText(MainActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             });
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                                              .child(username.getText().toString());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue(UserData.class) != null) {
                                    final UserData userData = dataSnapshot.getValue(UserData.class);
                                    if (Objects.equals(userData.getUSERNAME(), username.getText().toString())) {
                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        mAuth.signInWithEmailAndPassword(userData.getEMAIL(),
                                                                         password.getText().toString())
                                             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                                     if (task.isSuccessful()) {
                                                         // Sign in success, update UI with the signed-in user's information
                                                         userData.setPASSWORD(password.getText().toString());
                                                         mydb.addUser(userData);
                                                         Cursor res = mydb.getUser();
                                                         res.moveToFirst();
                                                         final Context context = getApplicationContext();
                                                         res.moveToFirst();
                                                         Intent i = new Intent(context, Homepage_DrawerActivity.class);
                                                         startActivity(i);
                                                         MainActivity.this.finish();
                                                     } else {
                                                         // If sign in fails, display a message to the user.
                                                         progressBar.setVisibility(View.GONE);
                                                         Toast.makeText(MainActivity.this, "Authentication failed.",
                                                                        Toast.LENGTH_SHORT).show();
                                                     }
                                                 }
                                             });
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Username Does not exist",
                                                   Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getApplicationContext();
                Intent i = new Intent(context, SignUpActivity.class);
                startActivity(i);
            }
        });
        fgpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotpassword();
            }
        });
    }

    public void forgotpassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.getname, null);
        final EditText email = view.findViewById(R.id.getmedname);
        email.setHint("Email");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (email.getText().toString().equals("")) {
                    email.requestFocus();
                    Toast.makeText(getApplicationContext(), "Email required", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isEmail(email.getText().toString())) {
                    email.requestFocus();
                    Toast.makeText(getApplicationContext(), "Email Address is not valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar.make(getCurrentFocus(),"Email sent",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setView(view);
        builder.setTitle("Email Address");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    boolean isEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
