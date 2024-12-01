package com.prac.network.service

import com.prac.network.dto.IssueDto
import com.prac.network.dto.PullDto
import com.prac.network.dto.ReadmeDto
import com.prac.network.dto.RepoDetailDto
import com.prac.network.dto.RepoDto
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
    ): List<RepoDto>

    @GET("repos/{userName}/{repoName}")
    suspend fun getRepo(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    ): RepoDetailDto

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
    ): List<IssueDto>

    @GET("repos/{userName}/{repoName}/pulls")
    suspend fun getRepoPulls(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    ): List<PullDto>

    @GET("repos/{userName}/{repoName}/readme")
    suspend fun getRepoReadme(
        @Path("userName") userName: String,
        @Path("repoName") repoName: String
    ): ReadmeDto
}