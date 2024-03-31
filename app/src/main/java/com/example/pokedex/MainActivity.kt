package com.example.pokedex

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ){
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable(
                        route = "home"
                    ){
                        Home(navController)
                    }

                    composable(
                        route = "battle/{name}?imageUrl={imageUrl}",
                        arguments = listOf(navArgument("imageUrl") { defaultValue = "" })
                    ){
                        backStackEntry ->
                        val name: String = backStackEntry.arguments?.getString("name") ?: ""
                        val imageUrl: String = backStackEntry.arguments?.getString("imageUrl") ?: ""
                        Battle(navController, name, imageUrl)
                    }
                }

            }
        }
    }
}