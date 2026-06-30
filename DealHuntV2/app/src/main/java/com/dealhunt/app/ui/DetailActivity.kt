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

            val gameId = intent.getStringExtra("GAME_ID") ?: ""
            val dealId = intent.getStringExtra("DEAL_ID") ?: ""
            val title  = intent.getStringExtra("GAME_TITLE") ?: "Oyun"

            b.toolbar.setNavigationOnClickListener { finish() }

            Toast.makeText(this, "ADIM 2 OK: gameId=$gameId dealId=$dealId", Toast.LENGTH_LONG).show()
        } catch (e: Throwable) {
            Toast.makeText(this, "ADIM 2 HATA: ${e::class.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
