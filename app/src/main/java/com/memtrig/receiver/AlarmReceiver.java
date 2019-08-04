package com.memtrig.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.memtrig.R;
import com.memtrig.activities.CreateTask;
import com.memtrig.activities.MainActivity;
import com.memtrig.globals.AlarmHelpers;
import com.memtrig.globals.App;
import com.memtrig.serializer.Task;

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private int myInt = -1;
    private Context mContext;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    public static Vibrator vibrator;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (!App.isLogin()) {
            return;
        }
        String key = intent.getStringExtra("task_key");
        Log.i("TAG", " key " + key);
        mContext = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks").child(key)
                .addListenerForSingleValueEvent(valueEventListener);
        Log.i("TAG", "alarm received ====================>>>>>>");
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.i("TAG", " on data change" + dataSnapshot);
            Task mTask = dataSnapshot.getValue(Task.class);
            if (mTask != null) {
                mTask.setKey(dataSnapshot.getKey());
                showNotification(mContext, mTask.getTitle(), mTask);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.i("ALARM_RECEIVER", " data " + dataSnapshot);
            Task mTask = dataSnapshot.getValue(Task.class);
            showNotification(mContext, mTask.getTitle(), mTask);
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

    private void showNotification(Context ctx, String message, Task task) {
        String CHANNEL_ID = null;
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CHANNEL_ID = "task alarm setting";
            CharSequence name = "Task Notification";
            String Description = "The task channel";
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), audioAttributes);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent snoozeIntent = new Intent(mContext, SnoozeReceiver.class);
        snoozeIntent.putExtra("task", task);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(mContext, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent completeIntent = new Intent(mContext, DoneReceiver.class);
        completeIntent.putExtra("task", task);
        PendingIntent donePendingIntent =
                PendingIntent.getBroadcast(mContext, 0, completeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "task_channel")
                .setSmallIcon(R.drawable.ic_checkmark_notification)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setOngoing(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent)
                .addAction(R.drawable.ic_checkmarked, "Complete", donePendingIntent)
                .setAutoCancel(false);

        notificationManager.notify(10001, builder.build());
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
        if (vibrator.hasVibrator()) {
            final int[] mAmplitudes = new int[]{0, 255, 0, 255, 0, 255, 0, 255, 0};

            if (Build.VERSION.SDK_INT >= 26) {
                createWaveFormVibrationUsingVibrationEffect();
            } else {
                vibrator.vibrate(pattern, 0);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createOneShotVibrationUsingVibrationEffect() {
        // 1000 : Vibrate for 1 sec
        // VibrationEffect.DEFAULT_AMPLITUDE - would perform vibration at full strength
        VibrationEffect effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
        vibrator.vibrate(effect);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createWaveFormVibrationUsingVibrationEffect() {
        long[] mVibratePattern = new long[]{0, 400, 1000, 600, 1000, 800, 1000, 1000};
        // -1 : Play exactly once
        VibrationEffect effect = VibrationEffect.createWaveform(mVibratePattern, 0);
        vibrator.vibrate(effect);
    }

    public static class SnoozeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TAG", " snooze clicked");
            vibrator.cancel();
            Task task = (Task) intent.getSerializableExtra("task");
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(10001);
            AlarmHelpers.startSnooze(context, task.getDate(), task.getTime(), task.getKey(),
                    task.getNotificationTime());
        }
    }

    public static class DoneReceiver extends BroadcastReceiver {

        private DatabaseReference mDatabase;
        private FirebaseAuth auth;

        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.i("TAG", " done clicked");
            vibrator.cancel();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            auth = FirebaseAuth.getInstance();
            Task task = (Task) intent.getSerializableExtra("task");
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(10001);
            task.setTaskDone(true);
            mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("tasks")
                    .child(task.getKey()).setValue(task)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "completed!", Toast.LENGTH_SHORT).show();
                            context.sendBroadcast(new Intent("refresh"));
                            context.sendBroadcast(new Intent("refresh_all"));
                        }
                    });

        }
    }
}
