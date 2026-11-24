package com.abryan.sportgrid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.abryan.sportgrid.ui.navigation.SportGridAppShell
import com.abryan.sportgrid.ui.theme.SportGridTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportGridTheme {
                SportGridAppShell()
            }
        }
    }
}
