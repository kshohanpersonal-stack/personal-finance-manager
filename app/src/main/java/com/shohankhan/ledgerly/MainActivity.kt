package com.shohankhan.ledgerly

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.shohankhan.ledgerly.ui.navigation.LedgerlyRoot
import com.shohankhan.ledgerly.ui.theme.LedgerlyTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LedgerlyTheme {
                LedgerlyRoot()
            }
        }
    }
}
