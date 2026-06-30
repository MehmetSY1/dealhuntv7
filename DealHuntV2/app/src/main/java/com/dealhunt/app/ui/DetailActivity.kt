package com.dealhunt.app.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dealhunt.app.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityDetailBinding
    private val vm: DetailViewModel by viewModels()

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        try {
            b = ActivityDetailBinding.inflate(layoutInflater)
            setContentView(b.root)

            val gameId = intent.getStringExtra("GAME_ID") ?: ""
            val dealId = intent.getStringExtra("DEAL_ID") ?: ""
            val title  = intent.getStringExtra("GAME_TITLE") ?: "Oyun"

            b.toolbar.setNavigationOnClickListener { finish() }

            Toast.makeText(this, "ADIM 3: ViewModel baglaniyor...", Toast.LENGTH_SHORT).show()

            vm.detail.observe(this) { state ->
                try {
                    when (state) {
                        is S.Loading -> {
                            Toast.makeText(this, "ADIM 3: Loading state OK", Toast.LENGTH_SHORT).show()
                        }
                        is S.Ok -> {
                            Toast.makeText(this, "ADIM 3: Ok state OK - ${state.data.platformPrices.size} fiyat", Toast.LENGTH_LONG).show()
                        }
                        is S.Err -> {
                            Toast.makeText(this, "ADIM 3: Err state: ${state.msg}", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                } catch (e: Throwable) {
                    Toast.makeText(this, "ADIM 3 OBSERVER HATA: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            if (gameId.isNotBlank()) vm.loadByGameId(gameId)
            else if (dealId.isNotBlank()) vm.loadByDealId(dealId, title, "")

        } catch (e: Throwable) {
            Toast.makeText(this, "ADIM 3 HATA: ${e::class.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
