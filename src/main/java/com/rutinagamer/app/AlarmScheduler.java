package com.rutinagamer.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import rutinagamer.modelo.Alarma;

public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";

    public static void programarAlarma(Context context, Alarma alarma) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e(TAG, "No se pueden programar alarmas exactas. Pidiendo permiso...");
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = alarma.getHorario().toSecondOfDay();
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        LocalTime horaAlarma = alarma.getHorario();
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaEjecucion = ahora.with(horaAlarma);

        if (proximaEjecucion.isBefore(ahora)) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }

        long triggerAtMillis = proximaEjecucion.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
            Log.i(TAG, "Alarma programada para: " + proximaEjecucion);
        } catch (SecurityException e) {
            Log.e(TAG, "Error de seguridad al programar alarma: " + e.getMessage());
        }
    }

    public static void cancelarAlarma(Context context, Alarma alarma) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = alarma.getHorario().toSecondOfDay();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.i(TAG, "Alarma cancelada para las: " + alarma.getHorario());
        }
    }

    public static void reprogramarTodas(Context context, List<Alarma> alarmas) {
        for (Alarma alarma : alarmas) {
            if (alarma.isActiva()) {
                programarAlarma(context, alarma);
            }
        }
    }
}
