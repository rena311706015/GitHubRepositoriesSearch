package repositories.search.network

import repositories.search.model.Branch
import repositories.search.model.Content
import repositories.search.model.Readme
import repositories.search.model.Repository
import repositories.search.model.response.CommitResponse
import repositories.search.model.response.RepositoryResponse
import retrofit2.Response

interface GithubRepo {
    suspend fun getRepositories(q: String, page: Int): Response<RepositoryResponse>
    suspend fun getRepository(owner: String, repo: String): Response<Repository>
    suspend fun getReadme(owner: String, repo: String): Response<Readme>
    suspend fun getContents(
        owner: String,
        repo: String,
        path: String,
        branch: String
    ): Response<List<Content>>

    suspend fun getBranches(owner: String, repo: String): Response<List<Branch>>

    //    suspend fun getCommits(owner: String, repo: String, branch: String): Response<List<Commit>>
    suspend fun getCommits(
        owner: String,
        repo: String,
        branch: String
    ): Response<List<CommitResponse>>
}