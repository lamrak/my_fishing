package net.validcat.fishing;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.validcat.fishing.db.Constants;
import net.validcat.fishing.db.DB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "FishingList";
    // findViewById
    @Bind(R.id.my_recycler_view)
    RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;

    List<FishingItem> itemsList = new ArrayList<FishingItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initDataBase();
        initUI();
    }

    private void initUI() {
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new FishingAdapter(this, itemsList);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fab_add_fishing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewActivity = new Intent(MainActivity.this, AddNewFishing.class);
                //TODO why do we do this here, in this class, not in AddNewFishing
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"); //TODO http://prntscr.com/7prdux
                final String date = sdf.format(new Date(System.currentTimeMillis()));
                startNewActivity.putExtra("keyDate", date);
                startActivityForResult(startNewActivity, Constants.ITEM_REQUEST);
            }
        });
    }

    private void initDataBase() {
        DB db = new DB(this);
        db.open();
        Cursor cursor = db.getAllData();
        if (cursor.moveToFirst()) itemsList = db.getData(itemsList, cursor);
        else cursor.close();
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.ITEM_REQUEST) {
            FishingItem item = new FishingItem(data);
            itemsList.add(item);
            adapter.notifyDataSetChanged();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            startActivity(new Intent(this, SettingsActivity.class));
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
