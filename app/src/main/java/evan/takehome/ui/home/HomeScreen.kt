package evan.takehome.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import evan.takehome.data.model.Repository
import evan.takehome.ui.components.RepositoryItem
import evan.takehome.ui.components.SearchBar
import evan.takehome.ui.components.UserHeader
import evan.takehome.ui.theme.BlueToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRepositoryClick: (Repository, Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Home", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlueToolbar
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSearch = { viewModel.searchUser(uiState.searchQuery) },
                modifier = Modifier.background(Color(0xFFF5F5F5))
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueToolbar)
                }
            } else {
                AnimatedVisibility(
                    visible = uiState.user != null,
                    enter = fadeIn(initialAlpha = 0f) + slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight }
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight }
                    )
                ) {
                    uiState.user?.let { user ->
                        LazyColumn {
                            item {
                                UserHeader(
                                    user = user,
                                    modifier = Modifier.background(Color(0xFFF5F5F5))
                                )
                            }

                            items(uiState.repositories) { repo ->
                                RepositoryItem(
                                    repository = repo,
                                    onClick = { onRepositoryClick(repo, uiState.totalForks) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
