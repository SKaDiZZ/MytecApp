package com.murtic.adis.techyz;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Preferences extends PreferenceActivity {

    private AppCompatDelegate appCompatDelegate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_layout);

        // Pokreni toolbar u preferences aktivnosti
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_preferences);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#F5F5F5"));
        toolbar.setTitleTextColor(Color.parseColor("#666666"));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        // Sta se dogadja kada korisnik klikne na back dugmic u toolbaru
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Ponovo pokreni glavnu aktivnost ovaj put sa novim postavkama
                        Intent restartMainActivity = new Intent(Preferences.this, MainActivity.class);
                        startActivity(restartMainActivity);

                    }
                }
        );


        // Dodaj Postavke fragment kao glavni sadrzaj u preferences aktivnosti
        getFragmentManager().beginTransaction()
                .replace(R.id.prefsFragment, new Postavke())
                .commit();

    }

    // Definisi Postavke fragment
    public static class Postavke extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Ucitaj listu postavki iz xml fajla prefrences_list.xml
            addPreferencesFromResource(R.xml.preferences_list);

            // Pronadji feedback opciju u listi postavki
            Preference userFeedback = findPreference("feedback");

            // Definisi onClickListener za feedback postavku, sta se desava kada kliknemo?
            userFeedback.setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {

                            // Pokreni novi intent
                            Intent sendMail = new Intent(Intent.ACTION_SEND);

                            // Ucitaj resurse tako da mozemo koristiti strings.xml
                            Resources resources = getResources();

                            // Definisimo varijable koje cemo ukljuciti u nas feedback email
                            // Ime aplikacije ucitavamo iz strings.xml fajla
                            String imeApplikacije = resources.getString(R.string.app_name);

                            // Verziju aplikacije ucitavamo iz strings.xml fajla
                            String verzijaAplikacije = resources.getString(R.string.app_version_info);

                            // Definisemo listu primaoca emaila
                            String [] primaoci = {"app@imtec.ba"};

                            // Sada sve pakujemo zajedno u intent sendMail
                            sendMail.putExtra(Intent.EXTRA_EMAIL, primaoci); // Dodajemo primaoce
                            sendMail.putExtra(Intent.EXTRA_SUBJECT, imeApplikacije + " " + verzijaAplikacije); // Dodajemo ime i verziju aplikacije u subject polje
                            sendMail.setType("text/html"); // Postavljamo tip podataka za poruku

                            // Startujemo menu koji ce izbaciti listu aplikacija za slanje email poruka a nakon izbora aplikacije startamo novu Compose aktivnost
                            startActivity(Intent.createChooser(sendMail, "Send Feedback:"));

                            return false;
                        }
                    }
            );

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void setSupportActionBar(Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {

        if (appCompatDelegate == null) {

            appCompatDelegate = AppCompatDelegate.create(this, null);

        }

        return appCompatDelegate;

    }

}