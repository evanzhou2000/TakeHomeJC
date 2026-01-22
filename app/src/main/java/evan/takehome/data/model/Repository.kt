package evan.takehome.data.model

import com.google.gson.annotations.SerializedName

data class Repository(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    @SerializedName("forks")
    val forks: Int
)
