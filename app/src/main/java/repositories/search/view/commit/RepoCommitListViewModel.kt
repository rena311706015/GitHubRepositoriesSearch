package repositories.search.view.commit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import repositories.search.model.response.CommitResponse
import repositories.search.network.GithubRepo

@KoinViewModel
class RepoCommitListViewModel(private val githubRepo: GithubRepo) : ViewModel() {

    val commits = MutableLiveData<List<CommitResponse>>()

    fun getCommits(owner: String, repo: String, branch: String = "") =
        viewModelScope.launch(Dispatchers.IO) {
            val result = githubRepo.getCommits(owner, repo, branch)
            if (result.isSuccessful) {
                commits.postValue(result.body())
            }
        }
}