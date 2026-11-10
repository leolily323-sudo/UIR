package com.sims.uir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.sims.uir.di.AppContainer
import com.sims.uir.ui.UIRApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dependencies = AppContainer.get(applicationContext)
        setContent {
            MaterialTheme {
                Surface {
                    UIRApp(
                        repository = dependencies.repository,
                        diagnosticsManager = dependencies.diagnosticsManager,
                    )
                }
            }
        }
    }
}
