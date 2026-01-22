package evan.takehome

import evan.takehome.data.model.Repository
import evan.takehome.data.model.User
import evan.takehome.data.repository.GitHubRepository
import evan.takehome.data.repository.Result
import evan.takehome.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var repository: GitHubRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchUser with blank userId shows error`() = runTest {
        viewModel = HomeViewModel(repository)

        viewModel.searchUser("")
        advanceUntilIdle()

        assertEquals("Please enter a user ID", viewModel.uiState.value.error)
    }

    @Test
    fun `searchUser success updates state with user and repos`() = runTest {
        val user = User(name = "Test User", avatarUrl = "https://example.com/avatar.png")
        val repos = listOf(
            Repository("Repo1", "Description 1", "2023-01-01T00:00:00Z", 100, 50),
            Repository("Repo2", "Description 2", "2023-01-02T00:00:00Z", 200, 75)
        )

        coEvery { repository.getUser("testuser") } returns Result.Success(user)
        coEvery { repository.getUserRepos("testuser") } returns Result.Success(repos)

        viewModel = HomeViewModel(repository)
        viewModel.searchUser("testuser")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.user)
        assertEquals("Test User", state.user?.name)
        assertEquals(2, state.repositories.size)
        assertEquals(125, state.totalForks) // 50 + 75
        assertNull(state.error)
    }

    @Test
    fun `searchUser calculates total forks correctly`() = runTest {
        val user = User(name = "Test User", avatarUrl = "https://example.com/avatar.png")
        val repos = listOf(
            Repository("Repo1", null, null, 100, 1000),
            Repository("Repo2", null, null, 200, 2000),
            Repository("Repo3", null, null, 300, 3000)
        )

        coEvery { repository.getUser("testuser") } returns Result.Success(user)
        coEvery { repository.getUserRepos("testuser") } returns Result.Success(repos)

        viewModel = HomeViewModel(repository)
        viewModel.searchUser("testuser")
        advanceUntilIdle()

        assertEquals(6000, viewModel.uiState.value.totalForks) // 1000 + 2000 + 3000
    }

    @Test
    fun `searchUser with user not found shows error`() = runTest {
        coEvery { repository.getUser("nonexistent") } returns Result.Error("User not found")

        viewModel = HomeViewModel(repository)
        viewModel.searchUser("nonexistent")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.user)
        assertEquals("User not found", state.error)
    }

    @Test
    fun `clearError clears error state`() = runTest {
        coEvery { repository.getUser("nonexistent") } returns Result.Error("User not found")

        viewModel = HomeViewModel(repository)
        viewModel.searchUser("nonexistent")
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `isLoading becomes false after fetching data completes`() = runTest {
        val user = User(name = "Test User", avatarUrl = "https://example.com/avatar.png")
        coEvery { repository.getUser("testuser") } returns Result.Success(user)
        coEvery { repository.getUserRepos("testuser") } returns Result.Success(emptyList())

        viewModel = HomeViewModel(repository)
        viewModel.searchUser("testuser")
        advanceUntilIdle()

        // After completion, loading should be false
        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.user)
    }
}
