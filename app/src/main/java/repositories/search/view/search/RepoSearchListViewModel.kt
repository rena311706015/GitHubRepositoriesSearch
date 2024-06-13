package repositories.search.view.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import repositories.search.model.Repository
import repositories.search.network.GithubRepo

@KoinViewModel
class RepoSearchListViewModel(private val githubRepo: GithubRepo) : ViewModel() {

    private val SEARCH_TRIGGER_DELAY_IN_MS = 1000L
    private var job: Job? = null
    val textState = MutableStateFlow("")
    val repositories = MutableLiveData<List<Repository>>()
    val errorType = MutableLiveData<ErrorType>()

    enum class ErrorType {
        REACH_LIMIT,
        EMPTY,
        OTHER
    }

    init {
        observeTextState()
    }

    @OptIn(FlowPreview::class)
    private fun observeTextState() {
        viewModelScope.launch {
            textState
                .debounce(SEARCH_TRIGGER_DELAY_IN_MS)
                .collect {
                    if (it.isNotEmpty() && it == textState.value) {
                        getSearchResult(it)
                    }
                }
        }
    }

    fun getSearchResult(text: String, page: Int = 1, loadMore: Boolean = false) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val result = githubRepo.getRepositories(text, page)
            when (result.code()) {
                in 200..300 -> {
                    val resultBody = result.body() ?: return@launch
                    if (loadMore) {
                        val newList = repositories.value!!.plus(resultBody.items)
                        repositories.postValue(newList)
                    } else {
                        if (resultBody.items.isEmpty()) {
                            errorType.postValue(ErrorType.EMPTY)
                        } else {
                            repositories.postValue(resultBody.items)
                        }
                    }
                }

                403 -> {
                    errorType.postValue(ErrorType.REACH_LIMIT)
                }

                else -> {
                    errorType.postValue(ErrorType.OTHER)
                }
            }
        }
    }
}
