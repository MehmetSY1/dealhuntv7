package com.dealhunt.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dealhunt.app.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityDetailBinding

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        try {
            b = ActivityDetailBinding.inflate(layoutInflater)
            setContentView(b.root)
            Toast.makeText(this, "Layout acildi - ADIM 1 basarili", Toast.LENGTH_LONG).show()
        } catch (e: Throwable) {
            Toast.makeText(this, "ADIM 1 HATA: ${e::class.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
