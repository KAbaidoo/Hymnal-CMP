package com.kobby.hymnal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.kobby.hymnal.core.database.DatabaseInitializer
import com.kobby.hymnal.presentation.screens.home.HomeScreen
import com.kobby.hymnal.start.StartScreen
import com.kobby.hymnal.theme.HymnalAppTheme
import kotlinx.coroutines.launch

@Composable
fun HymnalApp(databaseInitializer: DatabaseInitializer) {
    var isDatabaseInitialized by remember { mutableStateOf(false) }
    var initializationError by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // Initialize database on first composition
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                databaseInitializer.initialize()
                isDatabaseInitialized = true
            } catch (e: Exception) {
                initializationError = e.message
            }
        }
    }

    HymnalAppTheme {
        when {
            initializationError != null -> {
                // Show error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to initialize database: $initializationError",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            !isDatabaseInitialized -> {
                // Show loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            else -> {
                // Show main app
                val useCase = remember { ShowOnboarding.INSTANCE }
                val showOnboarding by useCase.execute().collectAsState(initial = false)

                if (showOnboarding) {
                    Navigator(StartScreen()) { navigator ->
                        SlideTransition(navigator)
                    }
                } else {
                    Navigator(HomeScreen()) { navigator ->
                        SlideTransition(navigator)
                    }
                }
            }
        }
    }
}