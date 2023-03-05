package com.example.qrscanner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity
{
    private AppBarConfiguration appBarConfiguration;

    public static BottomNavigationView bottomNavigationView;

    public static FirstFragment home = new FirstFragment();
    public static SecondFragment scanner = new SecondFragment();
    public static ThirdFragment attendance = new ThirdFragment();

    private int currentFragment = 1;
    //1 - home / 2 - scanner / 3 - attendance

    @Override
    protected void onPause() {
        super.onPause();
        if(home.isVisible() && !scanner.isVisible() && !attendance.isVisible())
        {
            currentFragment = 1;
        }
        if(!home.isVisible() && scanner.isVisible() && !attendance.isVisible())
        {
            currentFragment = 2;
        }
        if(!home.isVisible() && !scanner.isVisible() && attendance.isVisible())
        {
            currentFragment = 3;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentFragment == 1)
        {
            getSupportFragmentManager().beginTransaction().show(home).commit();

            getSupportFragmentManager().beginTransaction().hide(scanner).commit();
            getSupportFragmentManager().beginTransaction().hide(attendance).commit();
        }
        if(currentFragment == 2)
        {
            getSupportFragmentManager().beginTransaction().show(scanner).commit();

            getSupportFragmentManager().beginTransaction().hide(home).commit();
            getSupportFragmentManager().beginTransaction().hide(attendance).commit();
        }
        if(currentFragment == 3)
        {
            getSupportFragmentManager().beginTransaction().show(attendance).commit();

            getSupportFragmentManager().beginTransaction().hide(home).commit();
            getSupportFragmentManager().beginTransaction().hide(scanner).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("  Home");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment, home).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, scanner).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, attendance).commit();

        getSupportFragmentManager().beginTransaction().show(home).commit();
        getSupportFragmentManager().beginTransaction().hide(scanner).commit();
        getSupportFragmentManager().beginTransaction().hide(attendance).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.firstFragment:
                        actionBar.setTitle("  " + item.getTitle());
                        actionBar.setIcon(item.getIcon());
                        getSupportFragmentManager().beginTransaction().show(home).commit();

                        if(scanner.isVisible())
                        {
                            getSupportFragmentManager().beginTransaction().hide(scanner).commit();
                        }

                        if(attendance.isVisible())
                        {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            if (inputMethodManager.isAcceptingText()) {
                                View view = getCurrentFocus();
                                if(view != null)
                                {
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }
                            getSupportFragmentManager().beginTransaction().hide(attendance).commit();
                        }

                        return true;
                    case R.id.secondFragment:
                        actionBar.setTitle("  " + item.getTitle());
                        actionBar.setIcon(item.getIcon());
                        getSupportFragmentManager().beginTransaction().show(scanner).commit();

                        if(home.isVisible())
                        {
                            getSupportFragmentManager().beginTransaction().hide(home).commit();
                        }

                        if(attendance.isVisible())
                        {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            if (inputMethodManager.isAcceptingText()) {
                                View view = getCurrentFocus();
                                if(view != null)
                                {
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }
                            getSupportFragmentManager().beginTransaction().hide(attendance).commit();
                        }

                        return true;
                    case R.id.thirdFragment:
                        actionBar.setTitle("  " + item.getTitle());
                        actionBar.setIcon(item.getIcon());
                        getSupportFragmentManager().beginTransaction().show(attendance).commit();

                        if(home.isVisible())
                        {
                            getSupportFragmentManager().beginTransaction().hide(home).commit();
                        }

                        if(scanner.isVisible())
                        {
                            getSupportFragmentManager().beginTransaction().hide(scanner).commit();
                        }
                        return true;
                }
                return false;
            }
        });

//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        NavController navController = Navigation.findNavController(this, R.id.fragment);
//
//        appBarConfiguration = new AppBarConfiguration.Builder(R.id.firstFragment, R.id.secondFragment, R.id.thirdFragment).build();
//
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}