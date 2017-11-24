package com.example.bartek.podzialwydatkow;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; //Autentyfikacja Firebase
    private android.support.v7.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicjalizacja autentyfikacji Firebase
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Strona Główna");

        //Tabs
        mViewPager = findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }



    @Override
    public void onStart() {
        super.onStart();
        // Sprawdza czy użytkownik jest już zalogowany i od razu aktualizuje interfejs.
        FirebaseUser currentUser = mAuth.getCurrentUser(); //Pobiera usera z Firebase i zapisuje w zmiennej currentUser

        if(currentUser == null ){       //jeśli currentUser == null, to znaczy, ze jest wylogowany i przenosze go do strony logowania

            sendToStart();

        }


    }

    //Metoda przenosząca do startActivity
    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    //Menu w górnym prawym rogu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }


    //Wybor zakladki w menu głównym
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){      //Should've used switch here xD

                new AlertDialog.Builder(this).setTitle("Wylogowywanie")
                        .setIcon(R.drawable.ic_warning_purple_48dp)
                        .setMessage("Czy na pewno chcesz się wylogować?")
                        .setNegativeButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                sendToStart();
                            }
                        }).setPositiveButton("Nie", null).show();

        }



        if(item.getItemId() == R.id.main_setting_btn){

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if(item.getItemId() == R.id.main_users_btn){

            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Wyjście")
                .setIcon(R.drawable.ic_warning_purple_48dp)
                .setMessage("Czy na pewno chcesz wyjść z aplikacji?")
                .setNegativeButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setPositiveButton("Nie", null).show();
    }



}
