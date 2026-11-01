package com.zeniquill.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zeniquill.app.editor.EditorScreen
import com.zeniquill.app.editor.EditorViewModel
import com.zeniquill.app.highlight.TextMateRegistry
import com.zeniquill.app.settings.SettingsScreen
import com.zeniquill.app.ui.ZeniQuillTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Warm up TextMate grammar registry off the main thread.
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            TextMateRegistry.init(this@MainActivity)
        }

        setContent {
            ZeniQuillTheme {
                MainNavHost()
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val viewModel: EditorViewModel = viewModel()
    val appContext = androidx.compose.ui.platform.LocalContext.current.applicationContext

    // SAF "open document" launcher.
    var pendingOpen by remember { mutableStateOf(false) }
    val openFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.openUri(appContext, uri)
        }
    }

    LaunchedEffect(pendingOpen) {
        // no-op; launch handled by button callbacks below
    }

    NavHost(navController = navController, startDestination = "editor") {
        composable("editor") {
            EditorScreen(
                viewModel = viewModel,
                onOpenFile = {
                    openFileLauncher.launch(arrayOf("*/*"))
                },
                onOpenSettings = { navController.navigate("settings") },
                onOpenRecents = { navController.navigate("recents") },
            )
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable("recents") {
            RecentsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
