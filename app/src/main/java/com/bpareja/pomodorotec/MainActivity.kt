package com.bpareja.pomodorotec

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bpareja.pomodorotec.pomodoro.PomodoroScreen
import com.bpareja.pomodorotec.pomodoro.PomodoroViewModel
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat


class MainActivity : ComponentActivity() {

    private val viewModel: PomodoroViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroScreen(viewModel)
        }
        // Crear el canal de notificaciones
        createNotificationChannel()
        // Solicitar permiso para notificaciones en Android 13+
        requestNotificationPermission()

        }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alerta Pomodoro"
            val descriptionText = "Notificaciones importantes para tus sesiones de Pomodoro."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_light)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1500)

                // Usar el sonido de alarma predeterminado
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                setSound(
                    android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI,
                    audioAttributes
                )
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE
                )
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "pomodoro_channel"
        private const val REQUEST_CODE = 1
        const val NOTIFICATION_ID = 1
    }
}