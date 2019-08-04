package com.memtrig.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.memtrig.R;
import com.memtrig.globals.App;
import com.memtrig.serializer.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateTask extends AppCompatActivity {

    private TextView createTaskText;
    private TextInputEditText titleEditText;
    private TextInputEditText date;
    private TextInputEditText description;
    private AppCompatSpinner spinner;
    private CheckBox monday;
    private CheckBox tuesday;
    private CheckBox wednesday;
    private CheckBox thrusday;
    private CheckBox friday;
    private CheckBox saturday;
    private CheckBox sunday;
    private AppCompatButton saveButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String selectedTime;
    private String[] strings;
    private ProgressBar progressBar;
    private Calendar myCalendar;
    private String selectedDate = "";
    private String selectedHours = "";
    private boolean editMode = false;
    private Task mTask;
    private boolean sameTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.create_task);
        editMode = getIntent().getBooleanExtra("edit", false);
        createTaskText = findViewById(R.id.create_message);
        titleEditText = findViewById(R.id.title);
        myCalendar = Calendar.getInstance();
        date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CreateTask.this , dat, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        spinner = findViewById(R.id.notification_time);
        description = findViewById(R.id.description);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thrusday = findViewById(R.id.thrusday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);
        sunday = findViewById(R.id.sunday);
        saveButton = findViewById(R.id.save);
        progressBar = findViewById(R.id.progress_bar);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String title = titleEditText.getText().toString();
                String dueTime = selectedTime;
                String des = description.getText().toString();
                boolean mon = monday.isChecked();
                boolean tue = tuesday.isChecked();
                boolean wed = wednesday.isChecked();
                boolean thu = thrusday.isChecked();
                boolean fri = friday.isChecked();
                boolean sat = saturday.isChecked();
                boolean sun = sunday.isChecked();
                if (title == null || title.trim().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "please enter mTask title",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (selectedDate.equals("") || selectedDate.trim().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "please select Date",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (selectedHours.equals("") || selectedHours.trim().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "please select Time",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Task task = new Task();
                task.setTitle(title);
                task.setDate(selectedDate);
                task.setTime(selectedHours);
                task.setNotificationTime(dueTime);
                task.setDescription(des);
                task.setMonday(mon);
                task.setTuesday(tue);
                task.setWednesday(wed);
                task.setThrusday(thu);
                task.setFriday(fri);
                task.setSaturday(sat);
                task.setSunday(sun);
                if (editMode && !sameTime) {
                    App.taskAlarmSet(mTask.getKey(), false);
                }
                if (!editMode) {
                    task.setTaskDone(false);
                    String key = mDatabase.push().getKey();           //this returns the unique key generated by firebase
                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                            .child(key).setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content), "Success",
                                    Snackbar.LENGTH_SHORT).show();
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CreateTask.this.finish();
                                }
                            }, 800);
                        }
                    });
                } else {
                    task.setTaskDone(mTask.isTaskDone());
                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                            .child(mTask.getKey()).setValue(task)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content), "updated!!",
                                    Snackbar.LENGTH_SHORT).show();
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CreateTask.this.finish();
                                }
                            }, 500);
                        }
                    });
                }
            }
        });
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        int selection = 0;
        strings = getResources().getStringArray(R.array.time);
        if (editMode) {
            mTask = (Task) getIntent().getSerializableExtra("task");
            titleEditText.setText(mTask.getTitle());
            description.setText(mTask.getDescription());
            saveButton.setText("Update");
            createTaskText.setText("Update Task");
            date.setText(mTask.getDate() + " "+ mTask.getTime());
            monday.setChecked(mTask.isMonday());
            tuesday.setChecked(mTask.isTuesday());
            wednesday.setChecked(mTask.isWednesday());
            thrusday.setChecked(mTask.isThrusday());
            friday.setChecked(mTask.isFriday());
            saturday.setChecked(mTask.isSaturday());
            sunday.setChecked(mTask.isSunday());
            selectedDate = mTask.getDate();
            selectedHours = mTask.getTime();
            for (int i = 0; i < strings.length; i++) {
                String notificationTime = strings[i];
                if (notificationTime.split(" ")[0].equals(mTask.getNotificationTime())) {
                    selection = i;
                }
            }
        }
        spinner.setSelection(selection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTime = strings[i].split(" ")[0];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    DatePickerDialog.OnDateSetListener dat = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            String myFormat = "dd/MM/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            selectedDate = sdf.format(myCalendar.getTime());
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CreateTask.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String min = String.valueOf(selectedMinute);
                    if ((min).length() == 1) {
                        min = "0"+min;
                    }
                    selectedHours = selectedHour +":" + min;
                    date.setText(selectedDate + " " + selectedHours);
                    if (editMode) {
                        sameTime = false;
                    }
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(CreateTask.this)
                        .setTitle("Delete Task")
                        .setMessage(String.format("Are you sure you want to delete %s?", mTask.getTitle()))
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTask();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .show();
                return true;
                default:return super.onOptionsItemSelected(item);
        }
    }

    private void deleteTask() {
        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                .child(mTask.getKey()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(findViewById(android.R.id.content), "Deleted!!",
                                Snackbar.LENGTH_SHORT).show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CreateTask.this.finish();
                            }
                        }, 500);
                    }
                });
    }
}
