package evan.takehome

import evan.takehome.data.model.Repository
import evan.takehome.ui.detail.DetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DetailViewModelTest {

    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        viewModel = DetailViewModel()
    }

    @Test
    fun `setRepository updates state correctly`() {
        val repository = Repository(
            name = "Test Repo",
            description = "Test Description",
            updatedAt = "2023-01-01T00:00:00Z",
            stargazersCount = 100,
            forks = 50
        )

        viewModel.setRepository(repository, totalForks = 1000)

        val state = viewModel.uiState.value
        assertNotNull(state.repository)
        assertEquals("Test Repo", state.repository?.name)
        assertEquals(1000, state.totalForks)
        assertFalse(state.showStarBadge)
    }

    @Test
    fun `showStarBadge is true when totalForks exceeds 5000`() {
        val repository = Repository(
            name = "Test Repo",
            description = null,
            updatedAt = null,
            stargazersCount = 100,
            forks = 50
        )

        viewModel.setRepository(repository, totalForks = 5001)

        assertTrue(viewModel.uiState.value.showStarBadge)
    }

    @Test
    fun `showStarBadge is false when totalForks equals 5000`() {
        val repository = Repository(
            name = "Test Repo",
            description = null,
            updatedAt = null,
            stargazersCount = 100,
            forks = 50
        )

        viewModel.setRepository(repository, totalForks = 5000)

        assertFalse(viewModel.uiState.value.showStarBadge)
    }

    @Test
    fun `showStarBadge is false when totalForks is below 5000`() {
        val repository = Repository(
            name = "Test Repo",
            description = null,
            updatedAt = null,
            stargazersCount = 100,
            forks = 50
        )

        viewModel.setRepository(repository, totalForks = 4999)

        assertFalse(viewModel.uiState.value.showStarBadge)
    }

    @Test
    fun `showStarBadge is true for large fork counts`() {
        val repository = Repository(
            name = "Popular Repo",
            description = "Very popular repository",
            updatedAt = "2023-06-15T12:00:00Z",
            stargazersCount = 50000,
            forks = 10000
        )

        viewModel.setRepository(repository, totalForks = 100000)

        val state = viewModel.uiState.value
        assertTrue(state.showStarBadge)
        assertEquals(100000, state.totalForks)
    }
}
