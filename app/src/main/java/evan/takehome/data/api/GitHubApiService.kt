package evan.takehome.data.api

import evan.takehome.data.model.Repository
import evan.takehome.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path

// Retrofit Interface for api.github
interface GitHubApiService {

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): User

    @GET("users/{userId}/repos")
    suspend fun getUserRepos(@Path("userId") userId: String): List<Repository>

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}
