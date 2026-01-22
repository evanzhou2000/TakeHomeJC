package evan.takehome.data.repository

import evan.takehome.data.api.GitHubApiService
import evan.takehome.data.model.Repository
import evan.takehome.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class GitHubRepository(private val apiService: GitHubApiService) {

    suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val user = apiService.getUser(userId)
            Result.Success(user)
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> Result.Error("User not found")
                403 -> Result.Error("API rate limit exceeded. Please try again later.")
                else -> Result.Error("Error: ${e.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun getUserRepos(userId: String): Result<List<Repository>> = withContext(Dispatchers.IO) {
        try {
            val repos = apiService.getUserRepos(userId)
            Result.Success(repos)
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> Result.Error("Repositories not found")
                403 -> Result.Error("API rate limit exceeded. Please try again later.")
                else -> Result.Error("Error: ${e.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}
