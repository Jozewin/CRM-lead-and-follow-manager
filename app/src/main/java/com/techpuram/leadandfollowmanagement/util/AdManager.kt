package com.techpuram.leadandfollowmanagement.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"

    // Test ad unit IDs
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // Your production ad unit ID
    const val MAIN_SCREEN_AD = "ca-app-pub-5622787743640317/8330175254"

    private var interstitialAd: InterstitialAd? = null
    private var isInitialized = false

    fun initialize(context: Context, callback: () -> Unit = {}) {
        if (isInitialized) {
            callback()
            return
        }

        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "MobileAds initialization status: $initializationStatus")
            isInitialized = true
            callback()
        }
    }

    @Composable
    fun AdBanner(
        modifier: Modifier = Modifier,
        adId: String = TEST_BANNER_AD_UNIT_ID // Using test ad for debugging
    ) {
        val context = LocalContext.current
        var isAdLoading by remember { mutableStateOf(true) }
        var adLoaded by remember { mutableStateOf(false) }
        var adView: AdView? by remember { mutableStateOf(null) }

        // Properly dispose of AdView when composable is removed
        DisposableEffect(Unit) {
            onDispose {
                adView?.destroy()
                Log.d(TAG, "AdView destroyed")
            }
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp) // Standard banner height
        ) {
            if (isAdLoading && !adLoaded) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    AdView(ctx).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = adId
                        adView = this

                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                Log.d(TAG, "Banner ad loaded successfully")
                                isAdLoading = false
                                adLoaded = true
                            }

                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                Log.e(TAG, "Banner ad failed to load: ${adError.message}")
                                Log.e(TAG, "Error code: ${adError.code}")
                                isAdLoading = false
                                adLoaded = false
                            }

                            override fun onAdOpened() {
                                Log.d(TAG, "Banner ad opened")
                            }

                            override fun onAdClosed() {
                                Log.d(TAG, "Banner ad closed")
                            }
                        }

                        // Load the ad
                        val adRequest = AdRequest.Builder().build()
                        loadAd(adRequest)
                    }
                }
            )
        }
    }

    fun preloadInterstitialAd(context: Context) {
        if (!isInitialized) {
            Log.w(TAG, "AdManager not initialized, cannot preload interstitial")
            return
        }

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_AD_UNIT_ID, // Use test ad for debugging
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    Log.e(TAG, "Error code: ${loadAdError.code}")
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitialAd(context: Activity, onAdDismissed: () -> Unit = {}): Boolean {
        val ad = interstitialAd
        if (ad != null) {
            Log.d(TAG, "Showing interstitial ad")
            
            // Set up proper ad callbacks
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    interstitialAd = null
                    onAdDismissed()
                    // Preload next ad after current one is dismissed
                    preloadInterstitialAd(context)
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    onAdDismissed()
                    preloadInterstitialAd(context)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed full screen content")
                }
            }
            
            ad.show(context)
            return true
        } else {
            Log.d(TAG, "Interstitial ad not ready")
            onAdDismissed()
            preloadInterstitialAd(context)
            return false
        }
    }
}