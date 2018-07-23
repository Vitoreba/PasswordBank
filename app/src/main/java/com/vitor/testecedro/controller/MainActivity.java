package com.vitor.testecedro.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.vitor.testecedro.R;
import com.vitor.testecedro.controller.fragments.PersonFragment;
import com.vitor.testecedro.controller.fragments.SitesListFragment;
import com.vitor.testecedro.util.MainFragmentTabsManager;

public class MainActivity
        extends AppCompatActivity
        implements  BottomNavigationView.OnNavigationItemSelectedListener {

    private FrameLayout mFragment;
    private FloatingActionButton fabAddSite;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mFragment = findViewById(R.id.main_frame_layout);
        mBottomNavigationView = findViewById(R.id.main_navigation);

        fabAddSite = findViewById(R.id.fabAddSite);
        fabAddSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewSite(view);
            }
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        setCurrentFragment(MainFragmentTabsManager.MainFragmentType.MAIN_FRAGMENT_HOME);
    }

    private void addNewSite(View view) {
        Intent intent = new Intent(this, NewSiteActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                fabAddSite.show();
                setCurrentFragment(MainFragmentTabsManager.MainFragmentType.MAIN_FRAGMENT_HOME);
                return true;
            case R.id.navigation_person:
                fabAddSite.hide();
                setCurrentFragment(MainFragmentTabsManager.MainFragmentType.MAIN_FRAGMENT_PERSON);
                return true;
            default:
                break;
        }
        return false;
    }

    private void setCurrentFragment(MainFragmentTabsManager.MainFragmentType fragmentType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_frame_layout);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment newFragment = MainFragmentTabsManager.getInstance()
                .getMainFragmentByType(fragmentType);

        if (currentFragment == null) {
            fragmentTransaction.add(R.id.main_frame_layout, newFragment);
            fragmentTransaction.commit();
            MainFragmentTabsManager.getInstance().setCurrentSelectedFragment(newFragment);
        } else {
            if (!newFragment.isAdded()) {
                fragmentTransaction.add(R.id.main_frame_layout, newFragment);
            }

            int currentFragmentsCount = (fragmentManager.getFragments() != null)
                    ? fragmentManager.getFragments().size()
                    : 0;
            for (int i = 0; i < currentFragmentsCount; i++) {
                Fragment existentFragment = fragmentManager.getFragments().get(i);
                if (existentFragment != null) {
                    if (existentFragment instanceof SitesListFragment ||
                            existentFragment instanceof PersonFragment) {
                        fragmentTransaction.hide(existentFragment);
                    }
                }
            }
            fragmentTransaction.show(newFragment);
            MainFragmentTabsManager.getInstance().setCurrentSelectedFragment(newFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        // Criar alerta qdo for sair da Main
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set other dialog properties
        builder.setTitle("Sair da Aplicação");
        builder.setMessage("Deseja mesmo fazer logout?");

        // Add the buttons
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent intent = new Intent(MainActivity.this
                        , LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                // DO NOTHING
                dialog.cancel();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Show the Dialog
        dialog.show();
    }
}
