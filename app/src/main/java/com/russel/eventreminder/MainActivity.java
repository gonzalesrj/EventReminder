package com.russel.eventreminder;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    DatabaseHelper databaseHelper;

    ListView listView;
    ArrayList<Event> list;
    EventListAdapter adapter = null;
    ArrayAdapter<CharSequence> arrayAdapter;

    Uri imageUri;

    ImageView eventImage;
    EditText eventName, eventDate, eventDescription, eventReminder;
    Spinner eventRepeat;

    FloatingActionButton addEventButton;
    FloatingActionButton editImageFab;
    Button editEventButton;

    int year, month, day, hour, minute;
    int yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        showEventList();
        onClickButtonListener();
        onItemLongClickListener();


    }

    public void onClickButtonListener() {
        addEventButton = findViewById(R.id.add_event);
        addEventButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Add_Event.class);
                        startActivity(intent);
                    }
                }
        );

    }

    public void showEventList() {
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new EventListAdapter(MainActivity.this, R.layout.event_items, list);
        listView.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(MainActivity.this);

        Cursor cursor = databaseHelper.getData("SELECT ID, EventName, EventDate, EventDescription, EventReminder, EventRepeat, EventImage FROM EventTable");
        list.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String date = cursor.getString(2);
            String description = cursor.getString(3);
            String reminder = cursor.getString(4);
            String repeat = cursor.getString(5);
            byte[] image = cursor.getBlob(6);

            list.add(new Event(id, name, date, description, reminder, repeat, image));
        }

        adapter.notifyDataSetChanged();
    }

    public void onItemLongClickListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0) {
                            databaseHelper = new DatabaseHelper(MainActivity.this);
                            Cursor updateCursor = databaseHelper.getData("SELECT ID FROM EventTable");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (updateCursor.moveToNext()) {
                                arrID.add(updateCursor.getInt(0));
                            }
                            showDialogUpdate(MainActivity.this, arrID.get(position));
                        } else {
                            databaseHelper = new DatabaseHelper(MainActivity.this);
                            Cursor updateCursor = databaseHelper.getData("SELECT ID FROM EventTable");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (updateCursor.moveToNext()) {
                                arrID.add(updateCursor.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void showDialogUpdate(Activity activity, final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_activity);
        dialog.setTitle("Update");

        eventImage = dialog.findViewById(R.id.event_image3);
        eventName = dialog.findViewById(R.id.event_name3);
        eventDate = dialog.findViewById(R.id.event_date3);
        eventDescription = dialog.findViewById(R.id.event_description3);
        eventReminder = dialog.findViewById(R.id.event_reminder3);
        eventRepeat = dialog.findViewById(R.id.event_repeat3);
        editImageFab = dialog.findViewById(R.id.edit_image_button);
        editEventButton = dialog.findViewById(R.id.edit_event_button);


        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.repeat_values, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventRepeat.setAdapter(arrayAdapter);

        showDatePickerDialog();
        showDateTimePickerDialog();

        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;

        openGallery();

        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    databaseHelper.updateData(
                            position,
                            eventName.getText().toString().trim(),
                            eventDate.getText().toString().trim(),
                            eventDescription.getText().toString().trim(),
                            eventReminder.getText().toString().trim(),
                            eventRepeat.getSelectedItem().toString().trim(),
                            imageViewToByte(eventImage)
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("Update error", e.getMessage());
                }
                updateList();
            }
        });

        dialog.getWindow().setLayout(width, height);
        dialog.show();
    }

    public void showDialogDelete(final int id) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);
        dialogDelete.setTitle("Warning!");
        dialogDelete.setMessage("Are you sure you want to delete this?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    databaseHelper = new DatabaseHelper(MainActivity.this);
                    databaseHelper.deleteData(id);
                    Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                updateList();
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateList() {
        Cursor cursor = databaseHelper.getData("SELECT ID, EventName, EventDate, EventDescription, EventReminder, EventRepeat, EventImage FROM EventTable");
        list.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String date = cursor.getString(2);
            String description = cursor.getString(3);
            String reminder = cursor.getString(4);
            String repeat = cursor.getString(5);
            byte[] image = cursor.getBlob(6);

            list.add(new Event(id, name, date, description, reminder, repeat, image));
        }

        adapter.notifyDataSetChanged();
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void openGallery() {
        editImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        100
                );
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
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
        if(resultCode == RESULT_OK && requestCode == 100 && data != null) {
            imageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                eventImage.setImageBitmap(bitmap);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDatePickerDialog() {
        eventDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);

                        showDialog(1);
                    }
                }
        );
    }

    public void showDateTimePickerDialog() {
        eventReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 1)
            return new DatePickerDialog(MainActivity.this, datePickerListener, year, month, day);
        return null;
    }

    protected DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int years, int months, int days) {
            year = years;
            month = months + 1;
            day = days;
            eventDate.setText(year + "-" + month + "-" + day);
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, MainActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        eventReminder.setText(yearFinal + "-" + monthFinal + "-" + dayFinal + " " + hourFinal + " : " + minuteFinal);
    }
}
