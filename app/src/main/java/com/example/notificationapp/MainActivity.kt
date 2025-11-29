package com.example.notificationapp

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val restaurantName = "Mon Restaurant 🍴"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askNotificationPermission()
        createNotificationChannels()

        findViewById<Button>(R.id.btnOrderReady).setOnClickListener { showOrderReadyNotification() }
        findViewById<Button>(R.id.btnOrderLate).setOnClickListener { showOrderLateNotification() }
        findViewById<Button>(R.id.btnDish).setOnClickListener { showDishPopup() }
        findViewById<Button>(R.id.btnVip).setOnClickListener { showVipNotification() }
        findViewById<Button>(R.id.btnDelivery).setOnClickListener { showDeliveryNotification() }

        scheduleDailyDishRecommendation()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val attributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()

            // Commande prête
            val orderChannel = NotificationChannel("order_channel", "Commandes", NotificationManager.IMPORTANCE_HIGH)
            orderChannel.enableLights(true)
            orderChannel.lightColor = Color.GREEN
            manager?.createNotificationChannel(orderChannel)

            // Retard
            val lateChannel = NotificationChannel("late_channel", "Retards", NotificationManager.IMPORTANCE_HIGH)
            lateChannel.enableLights(true)
            lateChannel.lightColor = Color.RED
            val alertSound = Uri.parse("android.resource://${packageName}/raw/alert_sound")
            lateChannel.setSound(alertSound, attributes)
            manager?.createNotificationChannel(lateChannel)

            // Promotions
            val promoChannel = NotificationChannel("promo_channel", "Promotions", NotificationManager.IMPORTANCE_HIGH)
            promoChannel.enableLights(true)
            promoChannel.lightColor = Color.parseColor("#FFA500")
            val promoSound = Uri.parse("android.resource://${packageName}/raw/promo_sound")
            promoChannel.setSound(promoSound, attributes)
            manager?.createNotificationChannel(promoChannel)

            // VIP
            val vipChannel = NotificationChannel("vip_channel", "VIP / fidélité", NotificationManager.IMPORTANCE_HIGH)
            vipChannel.enableLights(true)
            vipChannel.lightColor = Color.MAGENTA
            vipChannel.enableVibration(true)
            vipChannel.vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
            val vipSound = Uri.parse("android.resource://${packageName}/raw/vip_sound")
            vipChannel.setSound(vipSound, attributes)
            manager?.createNotificationChannel(vipChannel)

            // Livraison
            val deliveryChannel = NotificationChannel("delivery_channel", "Livraison", NotificationManager.IMPORTANCE_HIGH)
            deliveryChannel.enableLights(true)
            deliveryChannel.lightColor = Color.BLUE
            manager?.createNotificationChannel(deliveryChannel)
        }
    }

    private fun showOrderReadyNotification() {
        val notification = NotificationCompat.Builder(this, "order_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$restaurantName - Commande prête ✅")
            .setContentText("Votre commande est prête à être récupérée !")
            .setColor(Color.GREEN)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(this).notify(301, notification)
    }

    private fun showOrderLateNotification() {
        val notification = NotificationCompat.Builder(this, "late_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$restaurantName - Commande en retard ⏱️")
            .setContentText("Votre commande prend un peu plus de temps. Merci de patienter !")
            .setColor(Color.RED)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(this).notify(302, notification)
    }

    private fun showDishNotification(dish: String = "Découvrez notre plat du jour !") {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "promo_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$restaurantName - Plat du jour 🍽️")
            .setContentText(dish)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .addAction(R.mipmap.ic_launcher, "Commander maintenant", pendingIntent)
            .setColor(Color.parseColor("#FFA500"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(this).notify(303, notification)
    }

    private fun showVipNotification() {
        val notification = NotificationCompat.Builder(this, "vip_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$restaurantName - VIP ⭐")
            .setContentText("Vous avez des points fidélité à utiliser !")
            .setColor(Color.MAGENTA)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(this).notify(304, notification)
    }

    private fun showDeliveryNotification() {
        val notification = NotificationCompat.Builder(this, "delivery_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$restaurantName - Livraison 🚚")
            .setContentText("Votre commande est en route et sera bientôt livrée !")
            .setColor(Color.BLUE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(this).notify(305, notification)
    }

    private fun showDishPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Plat du jour 🍽️")
        builder.setMessage("Voici notre plat du jour : Poulet rôti avec légumes !\nVoulez-vous commander maintenant ?")
        builder.setPositiveButton("Commander") { dialog, _ ->
            showDishNotification("Poulet rôti avec légumes")
            dialog.dismiss()
        }
        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    internal fun sendDishRecommendationNotification() {
        val dishes = listOf(
            "Poulet rôti avec légumes",
            "Pâtes carbonara",
            "Salade César",
            "Pizza Margherita",
            "Tajine de poulet aux olives"
        )
        val recommendedDish = dishes.random()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "promo_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$restaurantName - Recommandation du jour 🍽️")
            .setContentText("Aujourd'hui, essayez : $recommendedDish")
            .addAction(R.mipmap.ic_launcher, "Commander", pendingIntent)
            .setColor(Color.parseColor("#FFA500"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(this).notify(400, notification)
    }

    private fun scheduleDailyDishRecommendation() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(this, DishRecommendationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }


}

class DishRecommendationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val dishes = listOf(
                "Poulet rôti avec légumes",
                "Pâtes carbonara",
                "Salade César",
                "Pizza Margherita",
                "Tajine de poulet aux olives"
            )
            val recommendedDish = dishes.random()


            val intentMain = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intentMain, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(context, "promo_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Mon Restaurant 🍴 - Recommandation du jour 🍽️")
                .setContentText("Aujourd'hui, essayez : $recommendedDish")
                .addAction(R.mipmap.ic_launcher, "Commander", pendingIntent)
                .setColor(Color.parseColor("#FFA500"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            NotificationManagerCompat.from(context).notify(400, notification)
        }
    }


}
