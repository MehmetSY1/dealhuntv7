package com.dealhunt.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dealhunt.app.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        try {
            val b = ActivityDetailBinding.inflate(layoutInflater)
            setContentView(b.root)
            b.tvGameTitle.text = "ViewBinding calisiyor!"
            b.progressDetail.visibility = android.view.View.GONE
            b.contentLayout.visibility = android.view.View.VISIBLE
            b.tvError.visibility = android.view.View.GONE
            b.tvGenre.text = "Test"
            b.tvDescription.text = "ViewBinding basariyla yuklendi"
            b.tvBestPlatform.text = "Steam"
            b.tvBestPrice.text = "TL 100"
            b.tvPlatformCount.text = "3 platform"
            b.tvCheapestEver.text = "TL 50"
            b.tvPlatformCountSmall.text = "3 platform"
            Toast.makeText(this, "ViewBinding OK!", Toast.LENGTH_LONG).show()
        } catch (e: Throwable) {
            Toast.makeText(this, "HATA: ${e::class.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
