package com.example.githubrepositoriessearch.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.adapter.RepositoriesAdapter
import com.example.githubrepositoriessearch.databinding.FragmentRepoSearchBinding
import com.example.githubrepositoriessearch.model.Repository
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
class RepoSearchFragment  : Fragment() {
    private var _binding: FragmentRepoSearchBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var repoAdapter: RepositoriesAdapter
    private lateinit var repoList: List<Repository>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRepoSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            binding?.viewModel = sharedViewModel
//            binding?.searchProgressIndicator?.setProgressCompat(50,true)
            prepareRecyclerView()
            val searchEditText = binding.searchEditText
            searchEditText.doOnTextChanged { text, _, _, _ ->
                binding?.searchProgressIndicator?.bringToFront()
                binding?.searchProgressIndicator?.show()
                binding?.searchResult?.isVisible = false
                //always back to the first item after list items update
                //TODO 連續打字會觸發過多次的刷新
                repoAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        binding?.searchResult?.scrollToPosition(0)
                    }
                })
                viewModel?.getSearchResult(text.toString())
                viewModel?.repoListLiveData?.observe(viewLifecycleOwner) { list ->
                    if(repoList != list && !list.isNullOrEmpty()){
                        repoList = list
                        repoAdapter.submitList(repoList)
                        binding?.searchProgressIndicator?.hide()
                        binding?.searchResult?.isVisible = true
                    }
                }
            }
        }
    }

    private fun prepareRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        repoList = emptyList()
        repoAdapter = RepositoriesAdapter(RepositoriesAdapter.OnClickListener { repo ->
            val bundle = bundleOf("repo" to (repo.owner.login).plus(" ").plus(repo.name))
            view?.findNavController()?.navigate(R.id.action_repoSearchFragment_to_repoDetailFragment,bundle)
        })

        binding?.lifecycleOwner = this
        binding?.searchResult?.layoutManager = layoutManager
        binding?.searchResult?.adapter = repoAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}