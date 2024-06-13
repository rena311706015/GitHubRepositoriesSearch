package repositories.search.network

import repositories.search.model.Branch
import repositories.search.model.Content
import repositories.search.model.Readme
import repositories.search.model.Repository
import repositories.search.model.response.CommitResponse
import repositories.search.model.response.RepositoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    // Limitation: For unauthenticated requests, the rate limit allows you to make up to 10 requests per minute.

    @GET("/search/repositories")
    suspend fun getRepositories(
        @Query("q") q: String,
        @Query("page") page: Int
    ): Response<RepositoryResponse>

    @GET("/repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Repository>

    @GET("/repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<Readme>

    @GET("/repos/{owner}/{repo}/branches")
    suspend fun getBranches(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<List<Branch>>

//    @GET("/repos/{owner}/{repo}/commits")
//    suspend fun getCommits(
//        @Path("owner") owner: String,
//        @Path("repo") repo: String,
//        @Query("sha") branch: String
//    ): Response<List<Commit>>

    @GET("/repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("sha") branch: String
    ): Response<List<CommitResponse>>

    @GET("/repos/{owner}/{repo}/contents/{path}")
    suspend fun getContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Query("ref") branch: String
    ): Response<List<Content>>

}