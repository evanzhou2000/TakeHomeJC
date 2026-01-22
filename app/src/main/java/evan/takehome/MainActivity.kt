package evan.takehome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import evan.takehome.ui.navigation.NavGraph
import evan.takehome.ui.theme.TakeHomeComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as TakeHomeApplication).container

        setContent {
            TakeHomeComposeTheme(dynamicColor = false) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    gitHubRepository = container.gitHubRepository
                )
            }
        }
    }
}
