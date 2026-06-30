package com.dealhunt.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dealhunt.app.R
import com.dealhunt.app.databinding.ActivityMainBinding
import com.dealhunt.app.ui.fragments.*

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (s == null) show(HomeFragment())
        b.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home     -> show(HomeFragment())
                R.id.nav_explore  -> show(ExploreFragment())
                R.id.nav_wishlist -> show(WishlistFragment())
                R.id.nav_ai       -> show(AiFragment())
                R.id.nav_profile  -> show(ProfileFragment())
            }
            true
        }

        // Onceki cokme kaydi varsa goster
        checkLastCrash()
    }

    private fun checkLastCrash() {
        var crash: String? = null

        // Once SharedPreferences dene
        val prefs = getSharedPreferences("crash_log", android.content.Context.MODE_PRIVATE)
        crash = prefs.getString("last_crash", null)

        // Bulamazsa dosyadan dene
        if (crash == null) {
            try {
                val file = java.io.File(filesDir, "crash.txt")
                if (file.exists()) crash = file.readText()
            } catch (_: Exception) {}
        }

        if (crash != null) {
            android.app.AlertDialog.Builder(this)
                .setTitle("Son çökme kaydı")
                .setMessage(crash.take(3000))
                .setPositiveButton("Tamam") { _, _ ->
                    prefs.edit().remove("last_crash").apply()
                    try { java.io.File(filesDir, "crash.txt").delete() } catch (_: Exception) {}
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun show(f: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, f).commit()
    }
}
