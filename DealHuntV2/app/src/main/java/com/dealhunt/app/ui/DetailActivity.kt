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

            b.progressDetail.visibility = View.VISIBLE
            b.contentLayout.visibility = View.GONE
            b.tvError.visibility = View.GONE

            Toast.makeText(this, "ViewModel yukleniyor...", Toast.LENGTH_SHORT).show()

            vm.detail.observe(this) { state ->
                when (state) {
                    is S.Loading -> {
                        b.progressDetail.visibility = View.VISIBLE
                        b.contentLayout.visibility = View.GONE
                    }
                    is S.Ok -> {
                        b.progressDetail.visibility = View.GONE
                        b.contentLayout.visibility = View.VISIBLE
                        b.tvError.visibility = View.GONE
                        b.tvGameTitle.text = state.data.title
                        b.tvGenre.text = "Detay yuklendi"
                        b.tvDescription.text = "${state.data.platformPrices.size} platform bulundu"
                        b.tvBestPlatform.text = state.data.platformPrices.firstOrNull()?.storeName ?: "-"
                        b.tvBestPrice.text = state.data.platformPrices.firstOrNull()?.let {
                            com.dealhunt.app.data.TLRate.fmt(it.currentPrice)
                        } ?: "-"
                        b.tvPlatformCount.text = "${state.data.platformPrices.size} platform"
                        b.tvCheapestEver.text = state.data.cheapestEver
                        b.tvPlatformCountSmall.text = "${state.data.platformPrices.size} platform"
                        Toast.makeText(this, "Veri yuklendi!", Toast.LENGTH_SHORT).show()
                    }
                    is S.Err -> {
                        b.progressDetail.visibility = View.GONE
                        b.tvError.visibility = View.VISIBLE
                        b.tvError.text = state.msg
                        Toast.makeText(this, "Hata: ${state.msg}", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }

            when {
                gameId.isNotBlank() -> vm.loadByGameId(gameId)
                dealId.isNotBlank() -> vm.loadByDealId(dealId, title, "")
                else -> { b.tvError.text = "ID bulunamadi"; b.tvError.visibility = View.VISIBLE }
            }

        } catch (e: Throwable) {
            Toast.makeText(this, "HATA: ${e::class.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
