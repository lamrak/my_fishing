package net.validcat.fishing.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.validcat.fishing.FishingItem;
import net.validcat.fishing.R;
import net.validcat.fishing.data.FishingContract;
import net.validcat.fishing.data.Constants;
import net.validcat.fishing.tools.BitmapUtils;
import net.validcat.fishing.tools.CameraManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Denis on 11.09.2015.
 */
public class AddNewFishingFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String LOG_TAG = AddNewFishingFragment.class.getSimpleName();
    @Bind(R.id.iv_photo) ImageView ivPhoto;
    @Bind(R.id.et_place) EditText etPlace;
    @Bind(R.id.tv_date) TextView tvDate;
    @Bind(R.id.tv_weather) TextView tvWeather;
    @Bind(R.id.et_price) EditText etPrice;
    @Bind(R.id.et_details) EditText etDetails;

    private CameraManager cm;
    private Uri uri;
    private FishingItem item;
    private boolean userPhoto = false;
    private boolean updateData = false;

    public AddNewFishingFragment() {
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View addNewFragmentView = inflater.inflate(R.layout.add_new_fishing_fragment, container, false);
        ButterKnife.bind(this, addNewFragmentView);

        Intent intent = getActivity().getIntent();
        String strUri = intent.getStringExtra(Constants.DETAIL_KEY);

        if (!TextUtils.isEmpty(strUri)) {
            uri = Uri.parse(strUri);
            updateUiByItemId();

        }

       // fab_add_fishing_list.setOnClickListener(this);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogFragment() {

                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {

                        // Use the current date as the default date in the picker
                        final Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int day = c.get(Calendar.DAY_OF_MONTH);

                        // Create a new instance of DatePickerDialog and return it
                        return new DatePickerDialog(getActivity(), AddNewFishingFragment.this, year, month, day);
                    }
                }.show(getFragmentManager(), "datePicker");
            }
        });

        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis())));

        return addNewFragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.add_new_fishing_action_bar, menu);
//            finishCreatingMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_add_new_fishing:
                ContentValues cv = new ContentValues();
                if (this.item == null) {
                    this.item = new FishingItem();
                } else {
                    cv.put(FishingContract.FishingEntry._ID, item.getId());
                }

                cv.put(FishingContract.FishingEntry.COLUMN_PLACE, etPlace.getText().toString());
                cv.put(FishingContract.FishingEntry.COLUMN_DATE, tvDate.getText().toString());
                cv.put(FishingContract.FishingEntry.COLUMN_WEATHER, tvWeather.getText().toString());
                cv.put(FishingContract.FishingEntry.COLUMN_DESCRIPTION, etDetails.getText().toString());
                cv.put(FishingContract.FishingEntry.COLUMN_PRICE, etPrice.getText().toString());

                if (userPhoto) {
                    Bitmap photo = ((BitmapDrawable)ivPhoto.getDrawable()).getBitmap();
                    item.setBitmap(photo);
                    cv.put(FishingContract.FishingEntry.COLUMN_IMAGE,
                            BitmapUtils.convertBitmapToBiteArray(((BitmapDrawable) ivPhoto.getDrawable()).getBitmap()));
                }
                if (updateData){
                    getActivity().getContentResolver().update(FishingContract.FishingEntry.CONTENT_URI, cv,null,null);
                }else {
                    getActivity().getContentResolver().insert(FishingContract.FishingEntry.CONTENT_URI, cv);
                }

                getActivity().finish();
                break;

            case R.id.action_camera:
                cm = new CameraManager();
                cm.startCameraForResult(getActivity());
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Bitmap b = cm.getCameraPhoto();
            if (b != null) {
                userPhoto = true;
//              Bitmap rotate =  cm.rotateBitmap(b);
//                ivPhoto.setImageBitmap(CameraManager.scaleDownBitmap(rotate, Constants.HEIGHT_BITMAP, getActivity()));
                  b = CameraManager.scaleDownBitmap(b, Constants.HEIGHT_BITMAP, getActivity());
               // int rotate = rotate();
                  b = cm.rotateBitmap(b);
                  ivPhoto.setImageBitmap(b);
            } else {
                Log.d(LOG_TAG, "Intent data onActivityResult == null");
            }
        }
    }

    public void updateUiByItemId() {
        Cursor cursor = getActivity().getContentResolver().query(uri,
                FishingItem.COLUMNS, null, null, null);
        if(cursor != null){
            if (cursor.moveToFirst()) {
                etPlace.setText(cursor.getString(cursor.getColumnIndex(FishingContract.FishingEntry.COLUMN_PLACE)));
            }
        }else{
            cursor.close();
        }
        updateData = true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        tvDate.setText(String.format("%d.%d.%d", dayOfMonth, ++monthOfYear, year));
    }

//    private int rotate () {
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
//        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break; //Natural orientation
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break; //Landscape left
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;//Upside down
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;//Landscape right
//        }
//         int rotate = (info.orientation - degrees + 360) % 360;
//         return degrees;
//    }

}
