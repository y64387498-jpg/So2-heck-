package com.example.standoffhelper

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class OverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var bubble: TextView? = null
    private var timer: CountDownTimer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (bubble != null) return START_NOT_STICKY
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        bubble = TextView(this).apply {
            text = "Oyun yardımcısı\n02:00"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xDD182A3A.toInt())
            setPadding(22, 14, 22, 14)
        }
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 20
            y = 140
        }
        windowManager?.addView(bubble, params)
        timer = object : CountDownTimer(120_000, 1_000) {
            override fun onTick(ms: Long) {
                val s = ms / 1000
                bubble?.text = "Oyun yardımcısı\n%02d:%02d".format(s / 60, s % 60)
            }
            override fun onFinish() { bubble?.text = "Oyun açılıyor…" }
        }.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer?.cancel()
        bubble?.let { windowManager?.removeView(it) }
        bubble = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
