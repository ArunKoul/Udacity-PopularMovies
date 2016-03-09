package com.udacity.assigmment.popularmovies.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieFragment.OnMovieSelectedListener, FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            MovieFragment movieFragment = new MovieFragment();
            movieFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, movieFragment).commit();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieSelected(int position, MovieData movieData) {
        DetailFragment detailFrag = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);

        if (detailFrag != null) {
            detailFrag.updateMovieDetailView(movieData);

        } else {
            DetailFragment newFragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putSerializable(Constant.BUNDLE_ARG_DATA, movieData);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getSupportFragmentManager().addOnBackStackChangedListener(this);


    }

    @Override
    public void onBackStackChanged() {
        int stackHeight = getSupportFragmentManager().getBackStackEntryCount();
        if(getSupportActionBar() != null) {
            if (stackHeight > 0) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
            }
        }
    }
}
