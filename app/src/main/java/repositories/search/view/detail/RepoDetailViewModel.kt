package repositories.search.view.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import repositories.search.model.Readme
import repositories.search.model.Repository
import repositories.search.network.GithubRepo

@KoinViewModel
class RepoDetailViewModel(private val githubRepo: GithubRepo) : ViewModel() {

    val repository = MutableLiveData<Repository>()
    val readme = MutableLiveData<Readme>()

    fun getRepository(owner: String, repo: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = githubRepo.getRepository(owner, repo)
        if (result.isSuccessful) {
            repository.postValue(result.body())
        } else {
            Log.e("Rena", "getRepository failed: ${result.body().toString()}")
            Log.e("Rena", "getRepository failed: ${result.code()}")
        }
    }

    fun getReadme(owner: String, repo: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = githubRepo.getReadme(owner, repo)
        if (result.isSuccessful) {
            readme.postValue(result.body())
        }
    }
}