package com.example.githubrepositoriessearch.http

import com.example.githubrepositoriessearch.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BackendAPI {
    @GET("/search/repositories")
    suspend fun getRepositories(@Query("q") q: String): Response<RepositoryResults>

    @GET("/repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") o: String,
        @Path("repo") r: String
    ): Response<Repository>

    @GET("/repos/{owner}/{repo}/readme")
    suspend fun getREADME(@Path("owner") o: String, @Path("repo") r: String): Response<Readme>

    @GET("/repos/{owner}/{repo}/branches")
    suspend fun getBranches(
        @Path("owner") o: String,
        @Path("repo") r: String
    ): Response<List<Branch>>

    @GET("/repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") o: String,
        @Path("repo") r: String,
        @Query("sha") branch: String
    ): Response<List<CommitResult>>

    @GET("/repos/{owner}/{repo}/contents/{path}")
    suspend fun getContents(
        @Path("owner") o: String,
        @Path("repo") r: String,
        @Path("path") path: String,
        @Query("ref") branch: String
    ): Response<List<Content>>

}