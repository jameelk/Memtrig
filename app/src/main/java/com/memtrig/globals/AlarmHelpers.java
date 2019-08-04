package com.memtrig.globals;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;

import com.memtrig.receiver.AlarmReceiver;
import com.memtrig.receiver.MidnightAlarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AlarmHelpers {

    public static void start(Context context, String dateText,
                             String timeText, String taskKey, String snoozeTime) {
        PendingIntent pendingIntent;
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("task_key", taskKey);
        alarmIntent.putExtra("snooze_time", snoozeTime);
        pendingIntent = PendingIntent.getBroadcast(context, 1001, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar time = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        Date date = new Date();
        time.setTime(date);
        cal_now.setTime(date);
        Log.i("TAG", "hours " + date.getHours());
        String myDateFormat = "dd/MM/yy";
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);
        SimpleDateFormat timeFormatting = new SimpleDateFormat(timeFormat, Locale.US);
        try {
            Date date1 = sdf.parse(dateText);
            Date time1 = timeFormatting.parse(timeText);
            String day = (String) DateFormat.format("dd",   date1);
            String monthString  = (String) DateFormat.format("MM",  date1);
            String year = (String) DateFormat.format("yy", date1);
            String hour = (String) DateFormat.format("HH", time1);
            String mints = (String) DateFormat.format("mm", time1);
            Log.i("TAG", " date " + day + " month " + monthString + " year " + year + " hour "+ hour + " min "+ mints);
            time.set(Calendar.YEAR, Integer.parseInt("20"+year.trim()));
            time.set(Calendar.MONTH, (Integer.parseInt(monthString.trim())-1));
            time.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.trim()));
            time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.trim()));
            time.set(Calendar.MINUTE, Integer.parseInt(mints.trim()));
            time.set(Calendar.SECOND, 0);
            Log.i("AFTER", " date " + time.get(Calendar.DAY_OF_MONTH) + " month " + time.get(Calendar.MONTH)
                    + " year " + time.get(Calendar.YEAR) + " hour "+ time.get(Calendar.HOUR_OF_DAY)
                    + " min "+ time.get(Calendar.MINUTE));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
            } else {
                manager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
            }
            Log.i("TAG", " alarm is set" + time.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void startSnooze(Context context, String dateText,
                             String timeText, String taskKey, String snoozeTime) {
        PendingIntent pendingIntent;
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("task_key", taskKey);
        alarmIntent.putExtra("snooze_time", snoozeTime);
        pendingIntent = PendingIntent.getBroadcast(context, 1001, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar time = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        Date date = new Date();
        time.setTime(date);
        cal_now.setTime(date);
        Log.i("TAG", "hours " + date.getHours());
        String myDateFormat = "dd/MM/yy";
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat, Locale.US);
        SimpleDateFormat timeFormatting = new SimpleDateFormat(timeFormat, Locale.US);
        try {
            Date date1 = sdf.parse(dateText);
            Date time1 = timeFormatting.parse(timeText);
            String day = (String) DateFormat.format("dd",   date1);
            String monthString  = (String) DateFormat.format("MM",  date1);
            String year = (String) DateFormat.format("yy", date1);
            String hour = (String) DateFormat.format("HH", time1);
            String mints = (String) DateFormat.format("mm", time1);
            Calendar newCal = Calendar.getInstance();
            newCal.setTime(new Date());
            Log.i("TAG", " date " + day + " month " + monthString + " year " + year + " hour "+ hour + " min "+ mints);
            time.set(Calendar.YEAR, Integer.parseInt("20"+year.trim()));
            time.set(Calendar.MONTH, (Integer.parseInt(monthString.trim())-1));
            time.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.trim()));
            time.set(Calendar.HOUR_OF_DAY, newCal.get(Calendar.HOUR_OF_DAY));
            time.set(Calendar.MINUTE, newCal.get(Calendar.MINUTE));
            time.set(Calendar.SECOND, 0);
            time.add(Calendar.MINUTE, Integer.parseInt(snoozeTime.trim()));
            Log.i("AFTER", " date " + time.get(Calendar.DAY_OF_MONTH) + " month " + time.get(Calendar.MONTH)
                    + " year " + time.get(Calendar.YEAR) + " hour "+ time.get(Calendar.HOUR_OF_DAY)
                    + " min "+ time.get(Calendar.MINUTE));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (time.getTimeInMillis() ), pendingIntent);
            } else {
                manager.setExact(AlarmManager.RTC_WAKEUP, (time.getTimeInMillis()), pendingIntent);
            }
            Log.i("TAG", " alarm is set" + time.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void setMidNightAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent(context, MidnightAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    public static void setNextMidNightAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DATE, 1);
        Intent intent = new Intent(context, MidnightAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
        Log.i("AFTER", " date " + calendar.get(Calendar.DAY_OF_MONTH) + " month " + calendar.get(Calendar.MONTH)
                + " year " + calendar.get(Calendar.YEAR) + " hour "+ calendar.get(Calendar.HOUR_OF_DAY)
                + " min "+ calendar.get(Calendar.MINUTE));
    }

    public static void cancel(Context context) {
        PendingIntent pendingIntent;
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 1001, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
//        Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    public static boolean isAlarmSet(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);//the same as up
        boolean isWorking = (PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_NO_CREATE) != null);
//        Log.d("TAG", "alarm is " + (isWorking ? "" : "not") + " working...");
        return isWorking;
    }
}
