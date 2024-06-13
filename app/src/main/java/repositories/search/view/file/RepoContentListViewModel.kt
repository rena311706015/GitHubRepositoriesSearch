package repositories.search.view.file

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import repositories.search.model.Content
import repositories.search.network.GithubRepo


@KoinViewModel
class RepoContentListViewModel(private val githubRepo: GithubRepo) : ViewModel() {

    val contents = MutableLiveData<List<Content>>()
    fun getContents(owner: String, repo: String, path: String, branch: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val result = githubRepo.getContents(owner, repo, path, branch)
            if (result.isSuccessful) {
                val resultBody = result.body() ?: emptyList()
                val sortedContents = resultBody.sortedByDescending { it.isDir() }
                contents.postValue(sortedContents)
            }
        }
}