package com.dealhunt.app.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        Toast.makeText(this, "v4 acildi", Toast.LENGTH_LONG).show()

        val tv = TextView(this).apply {
            text = "Detay sayfasi v4\n\n" +
                "gameId=${intent.getStringExtra("GAME_ID")}\n" +
                "dealId=${intent.getStringExtra("DEAL_ID")}\n" +
                "title=${intent.getStringExtra("GAME_TITLE")}"
            textSize = 16f
            setPadding(40, 80, 40, 40)
            setTextColor(android.graphics.Color.WHITE)
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(android.graphics.Color.parseColor("#0A0C10"))
            addView(tv)
        }

        setContentView(scroll)
    }
}
