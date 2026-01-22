package evan.takehome

import evan.takehome.data.api.GitHubApiService
import evan.takehome.data.model.Repository
import evan.takehome.data.model.User
import evan.takehome.data.repository.GitHubRepository
import evan.takehome.data.repository.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class GitHubRepositoryTest {

    private lateinit var apiService: GitHubApiService
    private lateinit var repository: GitHubRepository

    @Before
    fun setup() {
        apiService = mockk()
        repository = GitHubRepository(apiService)
    }

    @Test
    fun `getUser returns success when API call succeeds`() = runTest {
        val user = User(name = "Test User", avatarUrl = "https://example.com/avatar.png")
        coEvery { apiService.getUser("testuser") } returns user

        val result = repository.getUser("testuser")

        assertTrue(result is Result.Success)
        assertEquals(user, (result as Result.Success).data)
    }

    @Test
    fun `getUser returns error when user not found`() = runTest {
        val response = Response.error<User>(404, "Not found".toResponseBody())
        coEvery { apiService.getUser("nonexistent") } throws HttpException(response)

        val result = repository.getUser("nonexistent")

        assertTrue(result is Result.Error)
        assertEquals("User not found", (result as Result.Error).message)
    }

    @Test
    fun `getUser returns error when rate limited`() = runTest {
        val response = Response.error<User>(403, "Rate limited".toResponseBody())
        coEvery { apiService.getUser("testuser") } throws HttpException(response)

        val result = repository.getUser("testuser")

        assertTrue(result is Result.Error)
        assertEquals("API rate limit exceeded. Please try again later.", (result as Result.Error).message)
    }

    @Test
    fun `getUserRepos returns success when API call succeeds`() = runTest {
        val repos = listOf(
            Repository("Repo1", "Description 1", "2023-01-01T00:00:00Z", 100, 50),
            Repository("Repo2", "Description 2", "2023-01-02T00:00:00Z", 200, 75)
        )
        coEvery { apiService.getUserRepos("testuser") } returns repos

        val result = repository.getUserRepos("testuser")

        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
        assertEquals("Repo1", result.data[0].name)
    }

    @Test
    fun `getUserRepos returns error when repositories not found`() = runTest {
        val response = Response.error<List<Repository>>(404, "Not found".toResponseBody())
        coEvery { apiService.getUserRepos("nonexistent") } throws HttpException(response)

        val result = repository.getUserRepos("nonexistent")

        assertTrue(result is Result.Error)
        assertEquals("Repositories not found", (result as Result.Error).message)
    }

    @Test
    fun `getUser returns network error on exception`() = runTest {
        coEvery { apiService.getUser("testuser") } throws java.net.UnknownHostException("No internet")

        val result = repository.getUser("testuser")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Network error"))
    }

    @Test
    fun `getUserRepos returns empty list for user with no repos`() = runTest {
        coEvery { apiService.getUserRepos("emptyuser") } returns emptyList()

        val result = repository.getUserRepos("emptyuser")

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
}
