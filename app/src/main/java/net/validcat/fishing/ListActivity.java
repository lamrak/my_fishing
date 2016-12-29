package net.validcat.fishing;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.validcat.fishing.data.Constants;
import net.validcat.fishing.fragments.ListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements ListFragment.IClickListener {
    public static final String LOG_TAG = ListActivity.class.getSimpleName();
    public static final String KEY_CLICKED_FRAGMENT = "clicked_fragment";
    public static final String F_DETAIL_TAG = "detail_fragment";

    @Bind(R.id.fab_add_fishing)
    FloatingActionButton fabAddFishing;

    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    public DrawerLayout drawer;
    @Bind(R.id.nv_view)
    public NavigationView navDrawer;

    private ActionBarDrawerToggle toggle;

    private boolean isTwoPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "WRITE_EXTERNAL_STORAGE is not granted");
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(LOG_TAG, "WRITE_EXTERNAL_STORAGE is requested");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.PERMISSIONS_REQUEST_WRITE_STORAGE);
//            }
            }
        }
        fabAddFishing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ListActivity.this, AddNewFishingActivity.class), Constants.ITEM_REQUEST);
            }
        });

//        if (findViewById(R.id.detail_fragment) != null) {
//            isTwoPanel = true;
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.detail_container, new DetailFragment(), F_DETAIL_TAG)
//                        .commit();
//
//                ListFragment lf = (ListFragment) getSupportFragmentManager()
//                        .findFragmentById(R.id.list_fragment);
//                //lf.setUseTabLayout(!twoPane);
//            }
//        } else {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.list_fragment, new ListFragment())
                .commit();
        isTwoPanel = false;
//

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0f);

        toggle = setupDrawerToggle();
        drawer.setDrawerListener(toggle);
        setupDrawerContent(navDrawer);

        setTitle(R.string.app_name);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        return true;
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                               @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults.length <= 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED)
                            Toast.makeText(this, R.string.storage_permissoin_denied, Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public void onItemClicked(long clickedItemId, View... sharedView) {
//        if (isTwoPanel) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(ListActivity.this, DetailActivity.class)
                        .putExtra(Constants.DETAIL_KEY, clickedItemId),
                        ActivityOptions.makeSceneTransitionAnimation(this,
                                new Pair<>(sharedView[0], sharedView[0].getTransitionName()),
                                new Pair<>(sharedView[1], sharedView[1].getTransitionName()),
                                new Pair<>(sharedView[2], sharedView[2].getTransitionName())).toBundle());
            } else startActivity(new Intent(ListActivity.this, DetailActivity.class)
                    .putExtra(Constants.DETAIL_KEY, clickedItemId));
//        } else {
//            Bundle args = new Bundle();
//            args.putLong(KEY_CLICKED_FRAGMENT, clickedItemId);
//
//            Fragment df = new DetailFragment();
//            df.setArguments(args);
//            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, df, TAG).commit();
//        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
            break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager()
                .findFragmentById(R.id.fragment_drawer).onActivityResult(requestCode, resultCode, data);
    }

}