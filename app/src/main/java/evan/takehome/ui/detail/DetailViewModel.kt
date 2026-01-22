package evan.takehome.ui.detail

import androidx.lifecycle.ViewModel
import evan.takehome.data.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DetailUiState(
    val repository: Repository? = null,
    val totalForks: Int = 0,
    val showStarBadge: Boolean = false
)

class DetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun setRepository(repository: Repository, totalForks: Int) {
        _uiState.value = DetailUiState(
            repository = repository,
            totalForks = totalForks,
            showStarBadge = totalForks > STAR_BADGE_THRESHOLD
        )
    }

    companion object {
        const val STAR_BADGE_THRESHOLD = 5000
    }
}
