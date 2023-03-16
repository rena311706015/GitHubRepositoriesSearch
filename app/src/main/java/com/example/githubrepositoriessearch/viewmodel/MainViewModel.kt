package com.example.githubrepositoriessearch.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubrepositoriessearch.adpter.Event
import com.example.githubrepositoriessearch.http.RetrofitInstance
import com.example.githubrepositoriessearch.model.Repository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val repoLiveData = MutableLiveData<List<Repository>>()
    val selected: MutableLiveData<Repository> = MutableLiveData()

    val openItemEvent: MutableLiveData<Event<Repository>> = MutableLiveData()

    fun getSearchResult(text: String) {
        viewModelScope.launch(IO) {

            Log.e("VM", "getSearchResult()")
            val result = RetrofitInstance.api.getRepositories(text)
            repoLiveData.postValue(result.body()?.items)
        }
    }

    fun observeRepoLiveData(): LiveData<List<Repository>> {
        return repoLiveData
    }

    fun openItem(item: Repository) {
        selected.value = item
        openItemEvent.value = Event(item)
    }
}