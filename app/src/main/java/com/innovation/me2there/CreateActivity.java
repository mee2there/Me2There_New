package com.innovation.me2there;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 */
public class CreateActivity extends FragmentActivity {
    private EditText title, desc, phone;
    private Button locationButton, createButton;
    private EditText activityDate;
    private Context context;
    private LatLng activityLocation;

    private int year;
    private int month;
    private int day;
    static final int DATE_DIALOG_ID = 999;
    private String currentDate;
    private static final int SELECT_PICTURE = 0;
    private static final int SELECT_LOCATION = 1;
    private Bitmap bitmap;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        context = this.getApplicationContext();
        title = (EditText) findViewById(R.id.edit_title);
        desc = (EditText) findViewById(R.id.edit_desc);
        activityDate = (EditText) findViewById(R.id.edit_date);


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
        activityDate.setText(currentDate);

        OnClickListener listenerDate = new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                showDialog(DATE_DIALOG_ID);
            }
        };
        activityDate.setOnClickListener(listenerDate);
        imageView = (ImageView) findViewById(R.id.actImage);
        phone = (EditText) findViewById(R.id.editPhone);
        phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast msg = Toast.makeText(getBaseContext(), "Only 10 numbers",
                        Toast.LENGTH_LONG);
                msg.show();
            }
        });



        locationButton = (Button) findViewById(R.id.pick_location);
        locationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLocation = (LatLng)getIntent().getParcelableExtra("currentLocation");
                Intent myIntent = new Intent(context, MapActivity.class);
                myIntent.putExtra("currentLocation",currentLocation);
                //startActivity(myIntent);
                startActivityForResult(myIntent, SELECT_LOCATION);
            }
        });
        createButton = (Button) findViewById(R.id.create_btn);
        createButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageId = DataStore.uploadImage(bitmap, title.getText().toString());
                EventDetailVO toCreate = new EventDetailVO(0, title.getText().toString(), desc.getText().toString(), new Date(year, month, day), activityLocation, title.getText().toString());
                DataStore.insertActivity(toCreate);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                bitmap = null;
                //Bitmap bitmap = getPath(data.getData());
                try {
                    bitmap = getBitmapFromUri(data.getData());
                } catch (IOException i) {
                    Log.i("Create Activity", "Could not get bitmap from uri for image: " + i.getMessage());
                    i.printStackTrace();
                } catch (Exception e) {
                    Log.i("Create Activity", "Could not get bitmap from uri for image: " + e.getMessage());
                    e.printStackTrace();
                }


                Log.i("Create Activity", "Uri: " + uri.toString());
                imageView.setImageBitmap(bitmap);
            }
        }
        if (requestCode == SELECT_LOCATION) {
            if(resultCode == RESULT_OK){
                activityLocation = data.getParcelableExtra("pick_location");
                if(activityLocation !=null){
                    locationButton.setVisibility(View.INVISIBLE);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void updateDisplay() {
        currentDate = new StringBuilder().append(month + 1).append("/")
                .append(day).append("/").append(year).toString();

        Log.i("DATE", currentDate);
    }

    DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int i, int j, int k) {

            year = i;
            month = j;
            day = k;
            updateDisplay();
            activityDate.setText(currentDate);
        }
    };


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, myDateSetListener, year, month,
                        day);
        }
        return null;
    }


    public void pickPhoto(View view) {
        //TODO: launch the photo picker
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);

    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void uploadPhoto(View view) {
        try {
            //executeMultipartPost();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}