package com.example.githubrepositoriessearch.viewmodel

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.githubrepositoriessearch.adapter.Event
import com.example.githubrepositoriessearch.http.RetrofitInstance
import com.example.githubrepositoriessearch.model.*
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel : ViewModel() {
    val repoListLiveData = MutableLiveData<List<Repository>>()
    val repoLiveData = MutableLiveData<Repository>()
    val repoReadmeLiveData = MutableLiveData<Readme>()
    val repoBranchListLiveData = MutableLiveData<List<Branch>>()
    val repoCommitListLiveData = MutableLiveData<List<CommitResult>>()
    val repoContentListLiveData = MutableLiveData<List<Content>>()
    val selected: MutableLiveData<Repository> = MutableLiveData()
    val openItemEvent: MutableLiveData<Event<Repository>> = MutableLiveData()
    val errorLiveData: MutableLiveData<ErrorBody?> = MutableLiveData()
    val moshi = Moshi.Builder().build()
    private var job: Job? = null

    fun getSearchResult(text: String) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getRepositories(text)
            if (result.isSuccessful) {
                repoListLiveData.postValue(result.body()?.items)
            } else {
                result.errorBody()?.string()?.let { errorProcess(it) }
            }
        }
    }

    fun observeRepoListLiveData(): LiveData<List<Repository>> {
        return repoListLiveData
    }

    fun getRepository(owner: String, repo: String) {
        viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getRepository(owner, repo)
            if (result.isSuccessful) {
                repoLiveData.postValue(result.body())
            } else {
                result.errorBody()?.string()?.let { errorProcess(it) }
            }
        }
    }

    fun getReadme(owner: String, repo: String) {
        viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getREADME(owner, repo)
            if (result.isSuccessful) {
                repoReadmeLiveData.postValue(result.body())
            } else {
                result.errorBody()?.string()?.let { errorProcess(it) }
            }
        }
    }

    fun getBranches(owner: String, repo: String) {
        viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getBranches(owner, repo)
            if (result.isSuccessful) {
                repoBranchListLiveData.postValue(result.body())
            } else {
                result.errorBody()?.string()?.let { errorProcess(it) }
            }
        }
    }

    fun observeBranchListLiveData(): LiveData<List<Branch>> {
        return repoBranchListLiveData
    }

    fun getCommits(owner: String, repo: String, branch: String) {
        viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getCommits(owner, repo, branch)
            if (result.isSuccessful) {
                repoCommitListLiveData.postValue(result.body())
            } else {
                result.errorBody()?.string()?.let { errorProcess(it) }
            }
        }
    }

    fun observeCommitListLiveData(): LiveData<List<CommitResult>> {
        return repoCommitListLiveData
    }

    fun getContents(owner: String, repo: String, path: String, branch: String) {
        viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getContents(owner, repo, path, branch)
            if (result.isSuccessful) {
                repoContentListLiveData.postValue(result.body())
            } else {
                result.errorBody()?.string()?.let { errorProcess(it) }
            }
        }
    }

    fun observeContentListLiveData(): LiveData<List<Content>> {
        return repoContentListLiveData
    }

    fun openItem(item: Repository) {
        selected.value = item
        openItemEvent.value = Event(item)
    }

    fun errorProcess(errorBody: String) {
        val fromJson = moshi.adapter(ErrorBody::class.java).fromJson(errorBody ?: "")
        errorLiveData.postValue(fromJson)
    }
}

@BindingAdapter("imageUrl")
fun loadImage(view: RoundedImageView?, url: String?) {
    if (!url.isNullOrEmpty()) {
        if (view != null) {
            Glide.with(view.context).load(url).into(view)
        }
    }
}
