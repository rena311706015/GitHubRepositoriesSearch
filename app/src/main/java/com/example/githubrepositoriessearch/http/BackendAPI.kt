package com.example.githubrepositoriessearch.http

import com.example.githubrepositoriessearch.model.Readme
import com.example.githubrepositoriessearch.model.Repository
import com.example.githubrepositoriessearch.model.Results
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BackendAPI {
    @GET("/search/repositories")
    suspend fun getRepositories(@Query("q") q : String) : Response<Results>

    @GET("/repos/{owner}/{repo}")
    suspend fun getRepository(@Path("owner") o: String, @Path("repo") r: String): Response<Repository>

    @GET("/repos/{owner}/{repo}/readme")
    suspend fun getREADME(@Path("owner") o: String, @Path("repo") r: String): Response<Readme>
}