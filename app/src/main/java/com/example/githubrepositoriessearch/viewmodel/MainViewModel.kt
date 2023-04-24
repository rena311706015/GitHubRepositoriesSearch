package com.example.githubrepositoriessearch.viewmodel

import android.app.Application
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.githubrepositoriessearch.adapter.Event
import com.example.githubrepositoriessearch.http.RetrofitInstance
import com.example.githubrepositoriessearch.model.*
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

var valueMap = HashMap<String, String>()
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    val repoListLiveData = MutableLiveData<List<Repository>>()
    val repoLiveData = MutableLiveData<Repository>()
    val repoReadmeLiveData = MutableLiveData<Readme>()
    val repoBranchListLiveData = MutableLiveData<List<Branch>>()
    val repoCommitListLiveData = MutableLiveData<List<CommitResult>>()
    val repoContentListLiveData = MutableLiveData<List<Content>>()
    var selectedRepo: MutableLiveData<Repository> = MutableLiveData()
    var selectedBranch: MutableLiveData<Branch> = MutableLiveData()
    var selectedContent: MutableLiveData<Content> = MutableLiveData()
    private val openRepoEvent: MutableLiveData<Event<Repository>> = MutableLiveData()
    private val openBranchEvent: MutableLiveData<Event<Branch>> = MutableLiveData()
    private val openContentEvent: MutableLiveData<Event<Content>> = MutableLiveData()

    val errorLiveData: MutableLiveData<ErrorBody?> = MutableLiveData()
    private val moshi: Moshi = Moshi.Builder().build()
    private var job: Job? = null

    init {
        viewModelScope.launch {
            // 在init時轉換一次就不用在bindingAdapter每次都轉換
            val jsonString = context.assets.open("language_color.json")
                .bufferedReader()
                .use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val keyIter: Iterator<String> = jsonObject.keys()
            var key: String
            var value: String
            while (keyIter.hasNext()){
                key = keyIter.next()
                value = jsonObject[key] as String
                valueMap[key] = value
            }
        }
    }

    fun getSearchResult(text: String) {
        //使用job，每當有新的request且前一個request還在執行，就會取消掉前一個request
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getRepositories(text)
            if (result.isSuccessful) {
                repoListLiveData.postValue(result.body()?.items)
            } else {
                errorLiveData.postValue(result.errorBody()?.string()?.let { errorProcess(it) })
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
                errorLiveData.postValue(result.errorBody()?.string()?.let { errorProcess(it) })
            }
        }
    }

    fun getReadme(owner: String, repo: String) {
        viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getREADME(owner, repo)
            if (result.isSuccessful) {
                selectedRepo.value?.readme = result.body()
                repoReadmeLiveData.postValue(result.body())
            } else {
                repoReadmeLiveData.postValue(Readme("",""))
            }
        }
    }

    fun getBranches(owner: String, repo: String) {
        viewModelScope.launch(IO) {
            val result = RetrofitInstance.api.getBranches(owner, repo)
            if (result.isSuccessful) {
                repoBranchListLiveData.postValue(result.body())
            } else {
                errorLiveData.postValue(result.errorBody()?.string()?.let { errorProcess(it) })
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
                errorLiveData.postValue(result.errorBody()?.string()?.let { errorProcess(it) })
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
                errorLiveData.postValue(result.errorBody()?.string()?.let { errorProcess(it) })
            }
        }
    }

    fun observeContentListLiveData(): LiveData<List<Content>> {
        return repoContentListLiveData
    }

    fun openItem(item: Repository) {
        selectedRepo.value = item
        openRepoEvent.value = Event(item)
    }

    fun openItem(item: Branch) {
        selectedBranch.value = item
        openBranchEvent.value = Event(item)
    }

    fun openItem(item: Content) {
        selectedContent.value = item
        openContentEvent.value = Event(item)
    }

    private fun errorProcess(errorBody: String) : ErrorBody?{
        val fromJson = moshi.adapter(ErrorBody::class.java).fromJson(errorBody)
        return fromJson
    }
}

//選擇language顏色
@BindingAdapter("circleColor")
fun loadCircleColor(view: ImageView?, lan: String?) {
    if (!lan.isNullOrEmpty()) {
        if (view != null) {
            valueMap[lan]?.toColorInt()?.let { view.background.setTint(it) }
        }
    }
}

//要載入圖片的時候，會用到這邊的adapter去轉換圖片url並顯示為傳入view的圖片
@BindingAdapter("imageUrl")
fun loadImage(view: RoundedImageView?, url: String?) {
    if (!url.isNullOrEmpty()) {
        if (view != null) {
            Glide.with(view.context).load(url).into(view)
        }
    }
}
