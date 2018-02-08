package com.abdelmun3m.movie_land.Views;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abdelmun3m.movie_land.Controlers.ControllerMain;
import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MoviewRecyclerView.MoviesAdapter;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.abdelmun3m.movie_land.utilities.MovieAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MoviesAdapter.MovieClick {




    //--------------------------------------
    private static final int POPULAR_LIST_SELECTION = 2;
    private static final int TOP_RATED_LIST_SELECTION = 1;
    private static final int FAVORITE_MOVIES_SELECTION = 3;
    private static final int ActivityLoaderID = 100;
    private int currentCategory = TOP_RATED_LIST_SELECTION;
    private static final String SORT_STORE_KEY = "SortKey";
    private static int SCREEN_SIZE = 1;
    private static final  int ERROR_EMPTY_FAVORITE_MOVIES = 1;
    private static final int ERROR_NO_CONNECTION_ = 2;
    private static final int ERROR_LOADING_ERROR = 3;
    //----------------------------------------
    private ControllerMain controller;
    private MoviesAdapter mAdapter;
    @BindView(R.id.rv_moviesView2)
    RecyclerView mRecyclerView ;
    @BindView(R.id.tv_ErrorMessage)
    TextView mErrorMessageField;


    GridLayoutManager manager;
    private Parcelable mListState;
    private String LIST_STATE_KEY = "state";
    private boolean dataloaded = false;
    List<Movie> movieList ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        changeSelectedListItem(1);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //------------------------------------------------------------------------

        controller = new ControllerMain(this);
        if(savedInstanceState == null) {
            setLayoutManager();
            controller.retrieveMovies(currentCategory);
        }
    }

    public void setDataloaded(Boolean loaded){
        this.dataloaded = loaded;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.top_rated) {
            // Handle the camera action
            currentCategory = TOP_RATED_LIST_SELECTION;
            controller.retrieveMovies(currentCategory);
        } else if (id == R.id.popular_movies) {
            currentCategory = POPULAR_LIST_SELECTION;
            controller.retrieveMovies(currentCategory);
        } else if (id == R.id.recommended) {
            //MovieAPI.Build_Movie_Recommendation("19404");
            controller.getMovieRecommendation("19404");
        } else if (id == R.id.about) {

        } else if (id == R.id.settings) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * used to change the background color of
     * drawer list items according to the current selected category
     * @input index represent index of item in the list
     * @output void
     * */
    public void changeSelectedListItem(int index){
        //findViewById(R.id.popular_movies).setBackgroundColor(Color.GRAY);
    }

    public void updateAdapterData(List<Movie> movies){
        if(mAdapter == null){
            mAdapter = new MoviesAdapter(null);
        }
        mAdapter.UpdateListOfMovies(movies);
        movieList = movies;
    }

    public void showErrorMsg(String msg){
        mRecyclerView.setVisibility(View.GONE);
        mErrorMessageField.setVisibility(View.VISIBLE);
        mErrorMessageField.setText(msg);
    }

    private void setLayoutManager(){
        int vertical = LinearLayoutManager.VERTICAL;
        int spanCount = 2;
        manager =new GridLayoutManager(this, spanCount, vertical,false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onMovieClick(Movie m) {

        controller.movieClicked(m);
    }

    public synchronized void loadPoster(Movie m, DynamicHeightNetworkImageView moviePoster, ProgressBar loader) {
            //controller.setMovieImages(m,moviePoster,loader);
        controller.loadImage(loader,m,moviePoster);
    }

    public void updateMovieInfo(ProgressBar loader, DynamicHeightNetworkImageView moviePoster, Movie m) {
        //TODO if you don not changed loading full info to detail activity
        //rememeber to move set dataloaded in loadMovieInfo inorder to not load movie
        // info agin in restoring activity state
          // controller.loadMovieInfo(loader,moviePoster,m);
    }

    public void movieUpdateListener(Movie m, long itemId) {
        //mAdapter.updateMovieView(m,itemId);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loaded",dataloaded);
        if(dataloaded){
            mListState = manager.onSaveInstanceState();
            outState.putParcelableArrayList("list",
                    (ArrayList<? extends Parcelable>) movieList);
            outState.putParcelable(LIST_STATE_KEY, mListState);
        }
        /*mListState = manager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY,  mListState);*/
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            dataloaded = savedInstanceState.getBoolean("loaded");
            if(dataloaded){
                mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
                movieList = savedInstanceState
                        .getParcelableArrayList("list");
                setLayoutManager();
                mAdapter.UpdateListOfMovies(movieList);
            }else {
                showErrorMsg("error");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            manager.onRestoreInstanceState(mListState);
        }
    }
}