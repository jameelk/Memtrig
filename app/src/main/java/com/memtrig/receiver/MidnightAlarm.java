package com.memtrig.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.memtrig.R;
import com.memtrig.activities.MainActivity;
import com.memtrig.globals.AlarmHelpers;
import com.memtrig.globals.App;
import com.memtrig.globals.Helpers;
import com.memtrig.serializer.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MidnightAlarm extends BroadcastReceiver {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String todaysDate;
    private Timer timer=new Timer();
    private final long DELAY = 1000;
    private ArrayList<Task> arrayList;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        arrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (!App.isTodayAlarmSet(Helpers.getToday()) && App.isLogin() && calendar.get(Calendar.HOUR_OF_DAY) < 2) {
            Log.i("TAG", " MIDNIGHT");
            App.todayAlarmState(Helpers.getToday(), true);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String myFormat = "dd/MM/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            todaysDate = sdf.format(calendar.getTime());
            auth = FirebaseAuth.getInstance();
            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks").orderByChild("time")
                    .addChildEventListener(childEventListener);
            calendar.get(Calendar.HOUR_OF_DAY);
            Log.i("TAG", " hour  "+ calendar.get(Calendar.HOUR_OF_DAY));
            AlarmHelpers.setMidNightAlarm(context);
        } else {
            AlarmHelpers.setNextMidNightAlarm(context);
        }
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.i("MAIN_ACTIVITY", " data " + dataSnapshot);
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                           processResultsOfToday(mContext);
                        }
                    },
                    DELAY
            );
            Task task = dataSnapshot.getValue(Task.class);
            task.setKey(dataSnapshot.getKey());
            if (task.getDate().equals(todaysDate)) {
                arrayList.add(task);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void processResultsOfToday(Context context) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < arrayList.size(); i++) {
            Task task = arrayList.get(i);
            switch (day) {
                case Calendar.SATURDAY:
                    if (task.isSaturday()) {
                        AlarmHelpers.start(context, task.getDate(), task.getTime(), task.getKey(),
                                task.getNotificationTime());
                        if (task.isTaskDone()) {
                            task.setTaskDone(false);
                            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                                    .child(task.getKey()).setValue(task)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("TAG", "set not done");
                                        }
                                    });
                        }
                    }
                    break;
                case Calendar.SUNDAY:
                    // Current day is Sunday
                    if (task.isSunday()) {
                        AlarmHelpers.start(context, task.getDate(), task.getTime(), task.getKey(),
                                task.getNotificationTime());
                        if (task.isTaskDone()) {
                            task.setTaskDone(false);
                            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                                    .child(task.getKey()).setValue(task)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("TAG", "set not done");
                                        }
                                    });
                        }
                    }
                    break;
                case Calendar.MONDAY:
                    // Current day is Monday
                    if (task.isMonday()) {
                        AlarmHelpers.start(context, task.getDate(), task.getTime(), task.getKey(),
                                task.getNotificationTime());
                        if (task.isTaskDone()) {
                            task.setTaskDone(false);
                            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                                    .child(task.getKey()).setValue(task)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("TAG", "set not done");
                                        }
                                    });
                        }
                    }
                    break;
                case Calendar.TUESDAY:
                    if (task.isTuesday()) {
                        AlarmHelpers.start(context, task.getDate(), task.getTime(), task.getKey(),
                                task.getNotificationTime());
                        if (task.isTaskDone()) {
                            task.setTaskDone(false);
                            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                                    .child(task.getKey()).setValue(task)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("TAG", "set not done");
                                        }
                                    });
                        }
                    }
                    // etc.
                    break;
                case Calendar.WEDNESDAY:
                    if (task.isWednesday()) {
                        AlarmHelpers.start(context, task.getDate(), task.getTime(), task.getKey(),
                                task.getNotificationTime());
                        if (task.isTaskDone()) {
                            task.setTaskDone(false);
                            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                                    .child(task.getKey()).setValue(task)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("TAG", "set not done");
                                        }
                                    });
                        }
                    }

                    break;
                case Calendar.THURSDAY:
                    if (task.isThrusday()) {
                        AlarmHelpers.start(context, task.getDate(), task.getTime(), task.getKey(),
                                task.getNotificationTime());
                        if (task.isTaskDone()) {
                            task.setTaskDone(false);
                            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                                    .child(task.getKey()).setValue(task)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("TAG", "set not done");
                                        }
                                    });
                        }
                    }

                    break;
                case Calendar.FRIDAY:
                    if (task.isFriday()) {
                        AlarmHelpers.start(context, task.getDate(), task.getTime(), task.getKey(),
                                task.getNotificationTime());
                        if (task.isTaskDone()) {
                            task.setTaskDone(false);
                            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                                    .child(task.getKey()).setValue(task)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("TAG", "set not done");
                                        }
                                    });
                        }
                    }

                    break;
            }
        }
    }

    private void showNotification(Context ctx, String message) {
        Calendar tomorrowCalendar = Calendar.getInstance();
        String CHANNEL_ID = null;
        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1);
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CHANNEL_ID = "Midnight alarm setting";
            CharSequence name = "Midnight";
            String Description = "The Midnight channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "midnight_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Today's Midnight alarm")
                .setChannelId(CHANNEL_ID)
                .setContentText(message);

        Intent resultIntent = new Intent(ctx, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        notificationManager.notify(12, builder.build());
    }
}
