package com.prac.data.source.network.service

import com.prac.data.BuildConfig
import com.prac.data.source.network.dto.AccessTokenRequest
import com.prac.data.source.network.dto.UserDto
import okhttp3.Credentials
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

internal interface GitHubUserService {
    @POST("applications/{clientID}/token")
    suspend fun getUserInformation(
        @Path("clientID") clientId: String = BuildConfig.CLIENT_ID,
        @Header("Accept") accept: String = "application/json",
        @Header("Authorization") authorization: String = Credentials.basic(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET),
        @Body accessToken: AccessTokenRequest
    ) : UserDto
}