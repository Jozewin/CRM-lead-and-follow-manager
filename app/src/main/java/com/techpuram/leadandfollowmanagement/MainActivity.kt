    package com.techpuram.leadandfollowmanagement

    import android.Manifest
    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.net.Uri
    import android.os.Build
    import android.os.Bundle
    import android.os.PowerManager
    import android.provider.Settings
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.annotation.RequiresApi
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.ui.Modifier
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.core.view.WindowCompat
    import com.techpuram.leadandfollowmanagement.presentation.navgraph.NavGraph
    import com.techpuram.leadandfollowmanagement.presentation.navgraph.Route
    import com.techpuram.leadandfollowmanagement.ui.theme.LeadAndFollowManagementTheme
    import com.techpuram.leadandfollowmanagement.util.RequestNotificationPermission
    import dagger.hilt.android.AndroidEntryPoint
    import androidx.compose.runtime.LaunchedEffect
    import com.techpuram.leadandfollowmanagement.util.AdManager

    @AndroidEntryPoint
    class MainActivity : ComponentActivity() {
        @RequiresApi(Build.VERSION_CODES.R)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            enableEdgeToEdge()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission()
            }
           // requestIgnoreBatteryOptimization()

            // Get followUpId from intent if present
            val followUpId = intent.takeIf { it.getBooleanExtra("fromNotification", false) }
                ?.getIntExtra("followUpId", -1)
                ?.takeIf { it != -1 }

            // Remove extras to avoid re-triggering navigation
            intent.removeExtra("fromNotification")
            intent.removeExtra("followUpId")

            AdManager.initialize(this)

            setContent {
                LeadAndFollowManagementTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                            val startDestination = Route.AppNavigation.route
                            NavGraph(startDestination = startDestination, followUpId = followUpId)
                        }
                    }
                }
            }
        }
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun requestNotificationPermission() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
        private fun requestIgnoreBatteryOptimization() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Handle exception
                    }
                }
            }
        }
    }

