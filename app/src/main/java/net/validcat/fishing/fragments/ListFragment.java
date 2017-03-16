package net.validcat.fishing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.validcat.fishing.ListActivity;
import net.validcat.fishing.R;
import net.validcat.fishing.adapters.FishingAdapter;
import net.validcat.fishing.data.FirebaseObjectManager;
import net.validcat.fishing.models.FishingItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListFragment extends Fragment { // implements LoaderManager.LoaderCallbacks<Cursor>
    public static final String LOG_TAG = ListActivity.class.getSimpleName();
    private static final int LOADER_ID = 3;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    @Bind(R.id.empty_view) TextView emptyView;
    private FishingAdapter adapter;
    private List<FishingItem> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        ButterKnife.bind(this, view);
        initUI();

        fetchFirebaseFishingData();

        return view;
    }

    private void fetchFirebaseFishingData() {
        FirebaseObjectManager dm = new FirebaseObjectManager();

        dm.retrieveFishingItem(new FirebaseObjectManager.IItemFetchedListener() {
            @Override
            public void onItemFetched(FishingItem fishingItem) {
                if (fishingItem != null) {
//                    items.clear();
                    items.add(fishingItem);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initUI() {
        items = new ArrayList<>();
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FishingAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);
        adapter.setIClickListener((IClickListener) getActivity());
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(getActivity(),
//                FishingContract.FishingEntry.CONTENT_URI,
//                FishingContract.FishingEntry.PROJECTION,
//                null, null, FishingContract.FishingEntry.COLUMN_DATE + " DESC");
//    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        adapter.swapCursor(data);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        adapter.swapCursor(null);
//    }

    public interface IClickListener {
        void onItemClicked(long id, View... sharedViews);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private boolean isFishingListEmpty() {
//        Cursor cursor = getActivity().getContentResolver().query(FishingContract.FishingEntry.CONTENT_URI,
//                FishingContract.FishingEntry.PROJECTION, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                int id = cursor.getInt(cursor.getColumnIndex(FishingContract.FishingEntry._ID));
//                Log.d(LOG_TAG, "Database is not empty = " + id);
//                cursor.close();
//                return false;
//            }
//            cursor.close();
//        }

        return true;
    }

    private void checkIsEmptyState() {
        boolean isEmpty = isFishingListEmpty();
//        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
//        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        checkIsEmptyState();
    }

}
