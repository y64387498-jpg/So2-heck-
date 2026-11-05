package com.example.standoffhelper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private var timer: CountDownTimer? = null
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 48, 32, 48)
            setBackgroundColor(0xFF101820.toInt())
        }
        val title = TextView(this).apply {
            text = "STANDOFF 2\nOYUN YARDIMCISI"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
        }
        status = TextView(this).apply {
            text = "Hazır"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(0xFFB8D8E8.toInt())
            setPadding(0, 28, 0, 28)
        }
        val permission = Button(this).apply {
            text = "Diğer uygulamaların üzerinde gösterme izni"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")))
                } else status.text = "Üstte gösterme izni zaten açık."
            }
        }
        val start = Button(this).apply {
            text = "BAŞLAT — 2 DAKİKA"
            textSize = 18f
            setOnClickListener { startCountdown() }
        }
        val stop = Button(this).apply {
            text = "DURDUR"
            setOnClickListener {
                timer?.cancel()
                stopService(Intent(this@MainActivity, OverlayService::class.java))
                status.text = "Durduruldu."
            }
        }
        layout.addView(title)
        layout.addView(status)
        layout.addView(permission)
        layout.addView(start)
        layout.addView(stop)
        setContentView(layout)
    }

    private fun startCountdown() {
        if (!Settings.canDrawOverlays(this)) {
            status.text = "Önce üstte gösterme iznini aç."
            return
        }
        startService(Intent(this, OverlayService::class.java).putExtra("seconds", 120))
        timer?.cancel()
        timer = object : CountDownTimer(120_000, 1_000) {
            override fun onTick(ms: Long) {
                val s = ms / 1000
                status.text = "Oyuna geçiş: %02d:%02d".format(s / 60, s % 60)
            }
            override fun onFinish() {
                status.text = "Standoff 2 açılıyor…"
                val launch = packageManager.getLaunchIntentForPackage("com.axlebolt.standoff2")
                if (launch != null) startActivity(launch)
                else status.text = "Standoff 2 bulunamadı. Oyunun yüklü olduğundan emin ol."
            }
        }.start()
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
