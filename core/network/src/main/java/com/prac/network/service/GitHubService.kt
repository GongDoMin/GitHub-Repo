package com.prac.network.service

import com.prac.network.model.response.IssueResponse
import com.prac.network.model.response.PullResponse
import com.prac.network.model.response.ReadmeResponse
import com.prac.network.model.response.RepoDetailResponse
import com.prac.network.model.response.RepoResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @GET("users/{userName}/repos")
    suspend fun getRepos(
        @Path("userName") userName: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): List<RepoResponse>

    @GET("repos/{userName}/{repoName}")
    suspend fun getRepo(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    ): RepoDetailResponse

    @GET("user/starred/{userName}/{repoName}")
    suspend fun isStarred(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    )

    @PUT("user/starred/{userName}/{repoName}")
    suspend fun starRepository(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    )

    @DELETE("user/starred/{userName}/{repoName}")
    suspend fun unStarRepository(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    )

    @GET("repos/{userName}/{repoName}/issues")
    suspend fun getRepoIssues(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    ): List<IssueResponse>

    @GET("repos/{userName}/{repoName}/pulls")
    suspend fun getRepoPulls(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    ): List<PullResponse>

    @GET("repos/{userName}/{repoName}/readme")
    suspend fun getRepoReadme(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    ): ReadmeResponse
}