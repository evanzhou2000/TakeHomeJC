package evan.takehome.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import evan.takehome.data.model.Repository
import evan.takehome.data.model.User
import evan.takehome.data.repository.GitHubRepository
import evan.takehome.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val repositories: List<Repository> = emptyList(),
    val error: String? = null,
    val totalForks: Int = 0,
    val searchQuery: String = "" // put search query there, for fixing the bug that search query
                                 // lost after returned from detail page to home page
)

class HomeViewModel(private val repository: GitHubRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun searchUser(userId: String) {
        if (userId.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a user ID")
            return
        }

        viewModelScope.launch {
            val currentQuery = _uiState.value.searchQuery
            _uiState.value = HomeUiState(isLoading = true, searchQuery = currentQuery)

            when (val userResult = repository.getUser(userId)) {
                is Result.Success -> {
                    when (val reposResult = repository.getUserRepos(userId)) {
                        is Result.Success -> {
                            val totalForks = reposResult.data.sumOf { it.forks }
                            _uiState.value = HomeUiState(
                                user = userResult.data,
                                repositories = reposResult.data,
                                totalForks = totalForks,
                                searchQuery = currentQuery
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = HomeUiState(
                                user = userResult.data,
                                error = reposResult.message,
                                searchQuery = currentQuery
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.value = HomeUiState(error = userResult.message, searchQuery = currentQuery)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    companion object {
        fun provideFactory(repository: GitHubRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
        }
    }
}
