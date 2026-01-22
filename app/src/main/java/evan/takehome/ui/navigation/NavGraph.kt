package evan.takehome.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import evan.takehome.data.model.Repository
import evan.takehome.data.repository.GitHubRepository
import evan.takehome.ui.detail.DetailScreen
import evan.takehome.ui.detail.DetailViewModel
import evan.takehome.ui.home.HomeScreen
import evan.takehome.ui.home.HomeViewModel
import java.net.URLDecoder
import java.net.URLEncoder

private const val EMPTY_PLACEHOLDER = "_EMPTY_"

object NavRoutes {
    const val HOME = "home"
    const val DETAIL = "detail/{name}/{description}/{updatedAt}/{stars}/{forks}/{totalForks}"

    fun detailRoute(
        name: String,
        description: String?,
        updatedAt: String?,
        stars: Int,
        forks: Int,
        totalForks: Int
    ): String {
        val encodedName = URLEncoder.encode(name, "UTF-8")
        val encodedDesc = URLEncoder.encode(description?.takeIf { it.isNotBlank() } ?: EMPTY_PLACEHOLDER, "UTF-8")
        val encodedUpdatedAt = URLEncoder.encode(updatedAt?.takeIf { it.isNotBlank() } ?: EMPTY_PLACEHOLDER, "UTF-8")
        return "detail/$encodedName/$encodedDesc/$encodedUpdatedAt/$stars/$forks/$totalForks"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    gitHubRepository: GitHubRepository
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        // for Home Page
        composable(
            route = NavRoutes.HOME
        ) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(gitHubRepository)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onRepositoryClick = { repo, totalForks ->
                    navController.navigate(
                        NavRoutes.detailRoute(
                            name = repo.name,
                            description = repo.description,
                            updatedAt = repo.updatedAt,
                            stars = repo.stargazersCount,
                            forks = repo.forks,
                            totalForks = totalForks
                        )
                    )
                }
            )
        }

        // for Detail Page
        composable(
            route = NavRoutes.DETAIL,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("updatedAt") { type = NavType.StringType },
                navArgument("stars") { type = NavType.IntType },
                navArgument("forks") { type = NavType.IntType },
                navArgument("totalForks") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val name = URLDecoder.decode(
                backStackEntry.arguments?.getString("name") ?: "",
                "UTF-8"
            )
            val descriptionRaw = URLDecoder.decode(
                backStackEntry.arguments?.getString("description") ?: "",
                "UTF-8"
            )
            val description = descriptionRaw.takeIf { it != EMPTY_PLACEHOLDER && it.isNotBlank() }

            val updatedAtRaw = URLDecoder.decode(
                backStackEntry.arguments?.getString("updatedAt") ?: "",
                "UTF-8"
            )
            val updatedAt = updatedAtRaw.takeIf { it != EMPTY_PLACEHOLDER && it.isNotBlank() }

            val stars = backStackEntry.arguments?.getInt("stars") ?: 0
            val forks = backStackEntry.arguments?.getInt("forks") ?: 0
            val totalForks = backStackEntry.arguments?.getInt("totalForks") ?: 0

            val repository = remember {
                Repository(
                    name = name,
                    description = description,
                    updatedAt = updatedAt,
                    stargazersCount = stars,
                    forks = forks
                )
            }

            val detailViewModel: DetailViewModel = viewModel()

            DetailScreen(
                viewModel = detailViewModel,
                repository = repository,
                totalForks = totalForks,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
