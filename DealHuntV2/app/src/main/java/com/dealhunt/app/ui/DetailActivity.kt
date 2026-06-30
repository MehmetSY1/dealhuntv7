package com.dealhunt.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dealhunt.app.R
import com.dealhunt.app.data.NetworkClient
import com.dealhunt.app.data.TLRate
import com.dealhunt.app.databinding.ActivityDetailBinding
import com.dealhunt.app.model.GameDetailUiState
import com.dealhunt.app.model.GameSearchResult
import com.dealhunt.app.util.WishlistManager

class DetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityDetailBinding
    private val vm: DetailViewModel by viewModels()
    private lateinit var adapter: PriceAdapter
    private var gameId = ""
    private var dealId = ""
    private var gameTitle = ""
    private var gameThumb = ""

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(b.root)

        gameId    = intent.getStringExtra("GAME_ID")    ?: ""
        dealId    = intent.getStringExtra("DEAL_ID")    ?: ""
        gameTitle = intent.getStringExtra("GAME_TITLE") ?: "Oyun"
        gameThumb = intent.getStringExtra("GAME_THUMB") ?: ""

        b.toolbar.setNavigationOnClickListener { finish() }

        try {
            if (gameThumb.isNotEmpty())
                Glide.with(this).load(gameThumb).placeholder(R.drawable.placeholder_game).centerCrop().into(b.ivGameHero)
        } catch (_: Throwable) {}

        adapter = PriceAdapter { price ->
            try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NetworkClient.dealUrl(price.dealId)))) } catch (_: Exception) {}
        }
        b.rvPrices.layoutManager = LinearLayoutManager(this)
        b.rvPrices.adapter = adapter

        b.btnRefresh.setOnClickListener { loadData() }

        updateFab()
        b.fabWishlist.setOnClickListener {
            val id = gameId.ifBlank { dealId }
            if (id.isBlank()) return@setOnClickListener
            val g = GameSearchResult(gameId = id, title = gameTitle, thumbnail = gameThumb)
            if (WishlistManager.has(this, id)) WishlistManager.remove(this, id)
            else WishlistManager.add(this, g)
            updateFab()
        }

        vm.detail.observe(this) { state ->
            if (isFinishing || isDestroyed) return@observe

            when (state) {
                is S.Loading -> {
                    b.progressDetail.visibility = View.VISIBLE
                    b.contentLayout.visibility = View.GONE
                    b.tvError.visibility = View.GONE
                }
                is S.Ok -> {
                    b.progressDetail.visibility = View.GONE
                    b.contentLayout.visibility = View.VISIBLE
                    b.tvError.visibility = View.GONE
                    renderSafely(state.data)
                }
                is S.Err -> {
                    b.progressDetail.visibility = View.GONE
                    b.contentLayout.visibility = View.GONE
                    b.tvError.visibility = View.VISIBLE
                    b.tvError.text = state.msg
                }
                else -> {}
            }
        }

        loadData()
    }

    // Her satiri ayri ayri koruyarak hangi alanin sorunlu oldugunu buluyoruz
    private fun renderSafely(d: GameDetailUiState) {
        runCatching { b.tvGameTitle.text = d.title }
            .onFailure { showCrashInfo("tvGameTitle", it) }

        runCatching { b.tvGenre.text = d.genre }
            .onFailure { showCrashInfo("tvGenre", it) }

        runCatching { b.tvDescription.text = d.description }
            .onFailure { showCrashInfo("tvDescription", it) }

        runCatching { b.tvPlatformCountSmall.text = "${d.platformPrices.size} platform" }
            .onFailure { showCrashInfo("tvPlatformCountSmall", it) }

        val best = d.platformPrices.firstOrNull()
        if (best != null) {
            runCatching { b.tvBestPlatform.text = best.storeName }
                .onFailure { showCrashInfo("tvBestPlatform", it) }

            runCatching { b.tvBestPrice.text = TLRate.fmt(best.currentPrice) }
                .onFailure { showCrashInfo("tvBestPrice/TLRate", it) }

            runCatching {
                if (best.logoUrl.isNotEmpty())
                    Glide.with(this).load(best.logoUrl).into(b.ivBestLogo)
            }.onFailure { showCrashInfo("ivBestLogo/Glide", it) }

            runCatching {
                if (best.originalPrice > best.currentPrice && best.savingsPercent > 0) {
                    b.tvBestDiscount.text = "-%${best.savingsPercent.toInt()}"
                    b.tvBestDiscount.visibility = View.VISIBLE
                    b.tvBestOriginal.text = TLRate.fmt(best.originalPrice)
                    b.tvBestOriginal.visibility = View.VISIBLE
                } else {
                    b.tvBestDiscount.visibility = View.GONE
                    b.tvBestOriginal.visibility = View.GONE
                }
            }.onFailure { showCrashInfo("tvBestDiscount/Original", it) }

            runCatching {
                b.btnBuyBest.setOnClickListener {
                    try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NetworkClient.dealUrl(best.dealId)))) } catch (_: Exception) {}
                }
            }.onFailure { showCrashInfo("btnBuyBest", it) }
        }

        runCatching { adapter.submitList(d.platformPrices) }
            .onFailure { showCrashInfo("adapter.submitList", it) }

        runCatching { b.tvCheapestEver.text = "${d.cheapestEver} (${d.cheapestEverDate})" }
            .onFailure { showCrashInfo("tvCheapestEver", it) }

        runCatching { b.tvPlatformCount.text = "${d.platformPrices.size} platformda mevcut" }
            .onFailure { showCrashInfo("tvPlatformCount", it) }
    }

    private fun showCrashInfo(field: String, e: Throwable) {
        runCatching {
            val prefs = getSharedPreferences("crash_log", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("last_crash", "ALAN: $field\nHATA: ${e::class.simpleName}: ${e.message}").apply()
        }
        runCatching {
            Toast.makeText(this, "HATA [$field]: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadData() {
        when {
            gameId.isNotBlank() -> vm.loadByGameId(gameId)
            dealId.isNotBlank() -> vm.loadByDealId(dealId, gameTitle, gameThumb)
            else -> {
                b.progressDetail.visibility = View.GONE
                b.tvError.visibility = View.VISIBLE
                b.tvError.text = "Oyun bilgisi bulunamadı"
            }
        }
    }

    private fun updateFab() {
        val id = gameId.ifBlank { dealId }
        if (id.isBlank()) return
        b.fabWishlist.setImageResource(
            if (WishlistManager.has(this, id)) R.drawable.ic_wishlist_filled
            else R.drawable.ic_wishlist_outline)
    }
}
