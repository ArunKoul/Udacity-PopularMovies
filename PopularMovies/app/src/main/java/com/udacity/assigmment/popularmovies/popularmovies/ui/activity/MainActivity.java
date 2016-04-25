package com.udacity.assigmment.popularmovies.popularmovies.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.udacity.assigmment.popularmovies.popularmovies.util.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieFragment.OnMovieSelectedListener, MovieFragment.OnHttpResponseListner,
        DetailFragment.OnMovieTrailerSelectedListener, FragmentManager.OnBackStackChangedListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @Bind(R.id.container)
    View container;
    @Bind(R.id.coordinator)
    View coordinatorLayout;

    @Nullable
    @Bind(R.id.detail_fragment_container)
    View detailFragmentContainer;
    MovieFragment mMovieFragment;
    DetailFragment mDetailFragment;
    private boolean mIsTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (container != null) {
            if (savedInstanceState != null) {
                return;
            }

            mMovieFragment = new MovieFragment();
            mMovieFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mMovieFragment).commit();
        }

        setSupportActionBar(toolbar);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (detailFragmentContainer != null) {
            mIsTabLayout = true;

            //if (savedInstanceState == null) {
            mDetailFragment = new DetailFragment();
            mDetailFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, mDetailFragment)
                    .commit();
            //}
        }

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

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
    public void onMovieSelected(MovieData movieData, boolean displayFristItem) {
        //DetailFragment detailFrag = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);

        //if (detailFrag != null) {
        if (mIsTabLayout) {
            //detailFrag.updateMovieDetailView(movieData);
            if ((mDetailFragment != null)) {
                //mDetailFragment.updateMovieDetailView(movieData);

                mDetailFragment.setData(movieData, mIsTabLayout);

                //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //transaction.replace(R.id.detail_fragment_container, mDetailFragment);
                //transaction.addToBackStack(null);
                //transaction.commit();
            }


        } else {

            if (!displayFristItem) {
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getSupportFragmentManager().addOnBackStackChangedListener(this);


    }

    @Override
    public void onBackStackChanged() {
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

}
