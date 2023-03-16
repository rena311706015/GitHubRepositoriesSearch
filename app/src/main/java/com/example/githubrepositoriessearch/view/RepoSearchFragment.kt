package com.example.githubrepositoriessearch.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositoriessearch.adpter.RepositoriesAdapter
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
            prepareRecyclerView()
            val searchEditText = binding.searchEditText
            searchEditText.doOnTextChanged { text, _, _, _ ->
                Log.e("onTextChanged", text.toString())
                viewModel?.getSearchResult(text.toString())
//                viewModel?.openItemEvent?.observe(viewLifecycleOwner) { event ->
//                    event.getContentIfNotHandled()?.let {
//                        requireActivity().supportFragmentManager
//                            .beginTransaction()
//                            .replace(
//                                com.google.android.material.R.id.container,
//                                RepoDetailFragment.newInstance()
//                            )
//                            .addToBackStack(null)
//                            .commit()
//                    }
//                }
                viewModel?.repoLiveData?.observe(viewLifecycleOwner) { list ->
                    Log.e("repolivedata","observe()")
                    repoList = list
//                    repoAdapter.setRepoList(repoList)
                    repoAdapter.submitList(repoList)
                }
            }
        }
    }

    private fun prepareRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        repoList = emptyList()
        repoAdapter = RepositoriesAdapter()

        binding?.lifecycleOwner = this
        binding?.searchResult?.layoutManager = layoutManager
        binding?.searchResult?.adapter = repoAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}