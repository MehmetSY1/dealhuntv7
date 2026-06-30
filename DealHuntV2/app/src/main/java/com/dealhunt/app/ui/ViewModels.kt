package com.dealhunt.app.ui

import androidx.lifecycle.*
import com.dealhunt.app.data.GameRepository
import com.dealhunt.app.data.TLRate
import com.dealhunt.app.model.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

sealed class S<out T> {
    object Idle : S<Nothing>()
    object Loading : S<Nothing>()
    data class Ok<T>(val data: T) : S<T>()
    data class Err(val msg: String) : S<Nothing>()
}

class MainViewModel : ViewModel() {
    private val repo = GameRepository()
    val search = MutableLiveData<S<List<GameSearchResult>>>(S.Idle)
    val deals  = MutableLiveData<S<List<DealDetail>>>(S.Loading)
    private var searchJob: Job? = null

    init { loadDeals() }

    fun loadDeals() {
        viewModelScope.launch {
            deals.value = S.Loading
            try {
                val list = repo.getFeaturedDeals()
                deals.value = if (list.isNotEmpty()) S.Ok(list) else S.Err("Fırsat bulunamadı")
            } catch (e: Throwable) {
                deals.value = S.Err("Yüklenemedi")
            }
        }
    }

    fun search(q: String) {
        if (q.isBlank()) { search.value = S.Idle; return }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            search.value = S.Loading
            try {
                val r = repo.search(q)
                search.value = if (r.isNotEmpty()) S.Ok(r) else S.Err("'$q' bulunamadı")
            } catch (e: Throwable) {
                search.value = S.Err("Bağlantı hatası")
            }
        }
    }

    fun clearSearch() { searchJob?.cancel(); search.value = S.Idle }
    fun searchGenre(g: String) = search(g)
}

class DetailViewModel : ViewModel() {
    // Tamamen bos - sadece class loading test
    val detail = MutableLiveData<S<GameDetailUiState>>(S.Loading)

    fun loadByGameId(gameId: String) {
        detail.value = S.Ok(GameDetailUiState(
            title = "Test Oyun ($gameId)",
            genre = "Test Genre",
            description = "Test aciklama",
            platformPrices = listOf(
                PlatformPrice(storeId="1", storeName="Steam", logoUrl="", currentPrice=10.0, originalPrice=20.0, savingsPercent=50.0, dealId="test123", isBestDeal=true)
            ),
            cheapestEver = "TL 5",
            cheapestEverDate = "2024"
        ))
    }

    fun loadByDealId(dealId: String, title: String, thumbnail: String) {
        detail.value = S.Ok(GameDetailUiState(
            title = title.ifBlank { "Test Oyun" },
            genre = "Test Genre",
            description = "Test aciklama",
            platformPrices = listOf(
                PlatformPrice(storeId="1", storeName="Steam", logoUrl="", currentPrice=10.0, originalPrice=20.0, savingsPercent=50.0, dealId=dealId, isBestDeal=true)
            ),
            cheapestEver = "TL 5",
            cheapestEverDate = "2024"
        ))
    }
}
