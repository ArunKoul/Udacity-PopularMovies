package com.udacity.assigmment.popularmovies.popularmovies.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.udacity.assigmment.popularmovies.popularmovies.R;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieContract;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieData;
import com.udacity.assigmment.popularmovies.popularmovies.ui.fragment.DetailFragment;
import com.udacity.assigmment.popularmovies.popularmovies.ui.fragment.MovieFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieFragment.OnMovieSelectedListener, MovieFragment.OnHttpResponseListner,
        DetailFragment.OnMovieTrailerSelectedListener, FragmentManager.OnBackStackChangedListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @Bind(R.id.container)
    View mContainer;
    @Bind(R.id.coordinator)
    View coordinatorLayout;


    @Nullable
    @Bind(R.id.detail_fragment_container)
    View detailFragmentContainer;

    MovieFragment mMovieFragment;
    DetailFragment mDetailFragment;
    private boolean mIsTabLayout;


    public static final String DETAILFRAGMENT_TAG = "DETAILFRAGMENT_TAG";
    public static final String MAINFRAGMENT_TAG = "MAINFRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (mContainer != null) {
            if (savedInstanceState != null) {
                updateHomeOption();
                return;
            }

            mMovieFragment = new MovieFragment();
            mMovieFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mMovieFragment, MAINFRAGMENT_TAG).commit();
        }

        if (detailFragmentContainer != null) {
            mIsTabLayout = true;

            if (savedInstanceState == null) {
                updateDetailFragment(null);
            }

            /*mDetailFragment = new DetailFragment();
            mDetailFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, mDetailFragment)
                    .commit();*/

            //updateDetailFragment(movieData);
        }

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieSelected(MovieData movieData, boolean displayFristItem, int position) {
        //mMovieFragment.mPosition = position;

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.movie_fragment);

        if(fragment != null) {
            ((MovieFragment)fragment).mPosition = position;
        }

        if (mIsTabLayout) {
            //if ((mDetailFragment != null)) {
                //mDetailFragment.setData(movieData, mIsTabLayout);

                updateDetailFragment(movieData);
            //Fragment detailFragment = getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);






            //}


        } else {

            if (!displayFristItem) {

                DetailFragment newFragment = DetailFragment.newInstance(movieData);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getSupportFragmentManager().addOnBackStackChangedListener(this);


    }

    @Override
    public void onBackStackChanged() {
        updateHomeOption();
    }

    private void updateHomeOption() {
        int stackHeight = getSupportFragmentManager().getBackStackEntryCount();
        if (getSupportActionBar() != null) {
            if (stackHeight > 0) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
            }
        }
    }

    @Override
    public void onSucess(MovieContract movieContract) {

    }

    @Override
    public void onError(int statusCode, String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mMovieFragment != null) {
                            mMovieFragment.updatePopularMovieRx();
                        }

                    }
                });

        snackbar.show();

    }

    @Override
    public void onMovieTrialerSelected(String trailerId) {
        watchYoutubeVideo(trailerId);
    }


    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    private void updateDetailFragment(MovieData movieData) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(movieData != null) {
            DetailFragment detailFragment = DetailFragment.newInstance(movieData);
            transaction.replace(R.id.detail_fragment_container, detailFragment, DETAILFRAGMENT_TAG);
            //transaction.addToBackStack(null);

        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(fragment != null) {
                transaction.remove(fragment);
            }
        }

        transaction.commit();
    }

}
