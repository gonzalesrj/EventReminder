package com.russel.eventreminder;

import android.Manifest;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Add_Event extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    DatabaseHelper databaseHelper;

    EditText nameEditText, dateEditText, descriptionEditText, dateTimeEditText;
    Spinner repeatSpinner;
    ArrayAdapter<CharSequence> adapter;

    ImageView imageView;
    Uri imageUri;
    String nullValue = "NULL";

    FloatingActionButton addImageFab;
    Button addEventButton;

    int year, month, day, hour, minute;
    int yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal;

    private static final int REQUEST_CODE_GALLERY = 999;
    private static final int DIALOG_ID_0 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__event);

        //TOOLBAR
        Toolbar editEventToolbar = findViewById(R.id.add_event_toolbar);
        setSupportActionBar(editEventToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //IMAGE VIEW
        imageView = findViewById(R.id.event_image);
        addImageFab = findViewById(R.id.add_image_button);

        //TEXT VIEW WITH ICONS


        //SPINNER WIDGET
        repeatSpinner = findViewById(R.id.event_repeat);
        adapter = ArrayAdapter.createFromResource(this, R.array.repeat_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(adapter);

        //CHOOSE IMAGE FROM GALLERY
        openGallery();

        //CHOOSE DATE EVENT
        showDatePickerDialog();

        //CHOOSE WHEN TO NOTIFY
        showDateTimePickerDialog();

        init();
        insertValues();
    }

    /*********** INITIALIZE TABLE COLUMN VALUES START **************/
    private void init() {
        databaseHelper = new DatabaseHelper(Add_Event.this);

        nameEditText = findViewById(R.id.event_name);
        dateEditText = findViewById(R.id.event_date);
        descriptionEditText = findViewById(R.id.event_description);
        dateTimeEditText = findViewById(R.id.event_reminder);
        repeatSpinner = findViewById(R.id.event_repeat);
        imageView = findViewById(R.id.event_image);

        addEventButton = findViewById(R.id.add_event_button);
    }

    private void insertValues() {
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    databaseHelper.insertData(
                            nameEditText.getText().toString().trim(),
                            dateEditText.getText().toString().trim(),
                            descriptionEditText.getText().toString().trim(),
                            dateTimeEditText.getText().toString().trim(),
                            repeatSpinner.getSelectedItem().toString().trim(),
                            imageViewToByte(imageView)

                    );
                    Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();
                    nameEditText.setText("");
                    dateEditText.setText("");
                    descriptionEditText.setText("");
                    dateTimeEditText.setText("");
                    repeatSpinner.setSelection(0);
                    imageView.setImageResource(0);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    /************* INITIALIZE TABLE COLUMN VALUES END **************/

    /*********** RETRIEVE IMAGE PERMISSION START ************/
    private void openGallery() {
        addImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        Add_Event.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_GALLERY && data != null) {
            imageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    /*********** RETRIEVE IMAGE PERMISSION END **************/

    /************ DATE AND TIME DIALOG FUNCTIONS START ******/
    public void showDatePickerDialog() {
        dateEditText = findViewById(R.id.event_date);
        dateEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);

                        showDialog(DIALOG_ID_0);
                    }
                }
        );
    }

    public void showDateTimePickerDialog() {
        dateTimeEditText = findViewById(R.id.event_reminder);
        dateTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Add_Event.this, Add_Event.this, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID_0)
            return new DatePickerDialog(Add_Event.this, datePickerListener, year, month, day);
        return null;
    }

    protected DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int years, int months, int days) {
            year = years;
            month = months + 1;
            day = days;
            dateEditText.setText(year + "-" + month + "-" + day);
        }
    };


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Add_Event.this, Add_Event.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        dateTimeEditText.setText(yearFinal + "-" + monthFinal + "-" + dayFinal + " " + hourFinal + " : " + minuteFinal);
    }

    /**************** DATE AND TIME FUNCTIONS END ****************/
}
