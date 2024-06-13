package repositories.search.network

import org.koin.core.annotation.Single
import repositories.search.model.Branch
import repositories.search.model.Content
import repositories.search.model.Readme
import repositories.search.model.Repository
import repositories.search.model.response.CommitResponse
import repositories.search.model.response.RepositoryResponse
import retrofit2.Response

@Single
class GithubRepoImpl(val githubApiStation: GithubApiStation) : GithubRepo {
    override suspend fun getRepositories(q: String, page: Int): Response<RepositoryResponse> =
        githubApiStation.repositoryApiService.getRepositories(q, page)

    override suspend fun getRepository(owner: String, repo: String): Response<Repository> =
        githubApiStation.repositoryApiService.getRepository(owner, repo)

    override suspend fun getReadme(owner: String, repo: String): Response<Readme> =
        githubApiStation.repositoryApiService.getReadme(owner, repo)

    override suspend fun getContents(
        owner: String,
        repo: String,
        path: String,
        branch: String,
    ): Response<List<Content>> =
        githubApiStation.repositoryApiService.getContents(owner, repo, path, branch)

    override suspend fun getBranches(owner: String, repo: String): Response<List<Branch>> =
        githubApiStation.repositoryApiService.getBranches(owner, repo)

//    override suspend fun getCommits(
//        owner: String,
//        repo: String,
//        branch: String,
//    ): Response<List<Commit>> =
//        githubApiStation.repositoryApiService.getCommits(owner, repo, branch)

    override suspend fun getCommits(
        owner: String,
        repo: String,
        branch: String,
    ): Response<List<CommitResponse>> =
        githubApiStation.repositoryApiService.getCommits(owner, repo, branch)

}