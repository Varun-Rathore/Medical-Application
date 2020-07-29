package com.example.medicalappv3.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.medicalappv3.Fragments.AccountFragment;
import com.example.medicalappv3.Fragments.DoctorFragment;
import com.example.medicalappv3.Fragments.OTPFragment;
import com.example.medicalappv3.Fragments.PatientFragment;
import com.example.medicalappv3.Fragments.ReminderFragment;
import com.example.medicalappv3.Fragments.SendPrescriptionFragment;
import com.example.medicalappv3.Fragments.SiteSelectFragment;
import com.example.medicalappv3.Fragments.ViewPatientDataFragment;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.ShowCheckHelper;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.WebPos;
import com.example.medicalappv3.WebFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class Homepage_DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static Integer current = 1;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    UserDataBaseHelper mydb;
    Cursor res;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_homepage);
        Intent i = getIntent();
        ShowDialog();
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                                                                 R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mydb = new UserDataBaseHelper(Homepage_DrawerActivity.this);
        res = mydb.getUser();
        res.moveToFirst();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().clear();
        if (res.getString(3).equals("Patient")) {
            navigationView.inflateMenu(R.menu.drawer_menu_pat);
        } else {
            navigationView.inflateMenu(R.menu.drawer_menu_doc);
        }
        TextView textView = navigationView.getHeaderView(0).findViewById(R.id.occupation);
        textView.setText(res.getString(3));
        textView = navigationView.getHeaderView(0).findViewById(R.id.header_name);
        textView.setText(res.getString(2));
        if (savedInstanceState == null) {
            if (res.getString(3).equals("Patient")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                       new PatientFragment()).commit();
                //removeMenu();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                       new DoctorFragment()).commit();
                //removeMenu();
            }
            navigationView.setCheckedItem(R.id.nav_home);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if (!task.isSuccessful()) {
                                     Toast.makeText(Homepage_DrawerActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });
    }

    void ShowDialog() {
        final ShowCheckHelper helper = new ShowCheckHelper(Homepage_DrawerActivity.this);
        Cursor hell = helper.getcheck();
        while (hell.moveToNext()) {
            if (hell.getString(0).equals("1")) {
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Homepage_DrawerActivity.this);
        View view = getLayoutInflater().inflate(R.layout.batterycheck, null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
                helper.addcheck("1");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setView(view);
        builder.setTitle("Remove Battery Optimization");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        toolbar.setTitle(item.getTitle());
        if (res.getString(3).equals("Patient")) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new PatientFragment()).commit();
                    current = 1;
                    break;
                case R.id.nav_otp:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new OTPFragment()).commit();
                    current = 2;
                    break;
                case R.id.nav_reminder:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new ReminderFragment()).commit();
                    current = 3;
                    break;
                case R.id.nav_account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new AccountFragment()).commit();
                    current = 4;
                    break;
                case R.id.nav_buy_med:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new SiteSelectFragment()).commit();
                    current = 5;
                    break;
            }
        } else {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new DoctorFragment()).commit();
                    current = 1;
                    break;
                case R.id.nav_revdata:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new ViewPatientDataFragment()).commit();
                    current = 2;
                    break;
                case R.id.nav_senddata:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new SendPrescriptionFragment()).commit();
                    current = 3;
                    break;
                case R.id.nav_account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new AccountFragment()).commit();
                    current = 4;
                    break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (current == 5) {
            if (WebFragment.webView.canGoBack()) {
                WebFragment.webView.goBack();
            } else if (WebPos.position != -1) {
                WebPos.position = -1;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                       new SiteSelectFragment()).commit();
            } else {
                if (res.getString(3).equals("Patient")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new PatientFragment()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                           new DoctorFragment()).commit();
                }
            }
        } else if (current != 1) {
            toolbar.setTitle("Home");
            if (res.getString(3).equals("Patient")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                       new PatientFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                       new DoctorFragment()).commit();
            }
        } else {
            super.onBackPressed();
        }

    }
}
