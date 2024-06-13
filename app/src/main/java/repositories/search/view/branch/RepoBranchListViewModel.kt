package repositories.search.view.branch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import repositories.search.model.Branch
import repositories.search.network.GithubRepo

@KoinViewModel
class RepoBranchListViewModel(private val githubRepo: GithubRepo) : ViewModel() {

    val branches = MutableLiveData<List<Branch>>()

    fun getBranches(owner: String, repo: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val result = githubRepo.getBranches(owner, repo)
            if (result.isSuccessful) {
                branches.postValue(result.body())
            }
        }
}