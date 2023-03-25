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
import com.example.githubrepositoriessearch.model.Readme
import com.example.githubrepositoriessearch.model.Repository
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val repoListLiveData = MutableLiveData<List<Repository>>()
    val repoLiveData = MutableLiveData<Repository>()
    val repoReadmeLiveData = MutableLiveData<Readme>()
    val selected: MutableLiveData<Repository> = MutableLiveData()
    val openItemEvent: MutableLiveData<Event<Repository>> = MutableLiveData()

    fun getSearchResult(text: String) {
        viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getRepositories(text)
            repoListLiveData.postValue(result.body()?.items)
        }
    }
    fun observeRepoListLiveData(): LiveData<List<Repository>> {
        return repoListLiveData
    }
    fun getRepository(owner:String, repo:String){
        viewModelScope.launch(IO){
            val result = RetrofitInstance.api.getRepository(owner, repo)
            repoLiveData.postValue(result.body())
        }
    }
    fun getReadme(owner:String, repo:String){
        viewModelScope.launch(IO){
            val result = RetrofitInstance.api.getREADME(owner, repo)
            repoReadmeLiveData.postValue(result.body())
        }
    }
    fun openItem(item: Repository) {
        selected.value = item
        openItemEvent.value = Event(item)
    }

}
@BindingAdapter("imageUrl")
fun loadImage(view: RoundedImageView?, url: String?){
    if(!url.isNullOrEmpty()){
        if (view != null) {
            Glide.with(view.context).load(url).into(view)
        }
    }
}
