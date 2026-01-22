package evan.takehome.di

import evan.takehome.data.api.GitHubApiService
import evan.takehome.data.repository.GitHubRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val gitHubRepository: GitHubRepository
}

class DefaultAppContainer : AppContainer {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(GitHubApiService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val gitHubApiService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }

    override val gitHubRepository: GitHubRepository by lazy {
        GitHubRepository(gitHubApiService)
    }
}
