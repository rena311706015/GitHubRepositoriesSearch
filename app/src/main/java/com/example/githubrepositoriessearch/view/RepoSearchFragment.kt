package com.example.githubrepositoriessearch.view

import android.app.AlertDialog
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
import com.example.githubrepositoriessearch.model.ErrorBody
import com.example.githubrepositoriessearch.model.Repository
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class RepoSearchFragment : Fragment() {
    private var _binding: FragmentRepoSearchBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var repoAdapter: RepositoriesAdapter
    private lateinit var repoList: List<Repository>
    private var errorBody: ErrorBody = ErrorBody()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRepoSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    //TODO Fragment按返回鍵後未銷毀
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.topAppBar?.visibility = View.GONE
        binding.apply {
            binding?.viewModel = sharedViewModel
            prepareRecyclerView()
            val searchEditText = binding.searchEditText
            searchEditText.doOnTextChanged { text, _, _, _ ->
                if(!text.isNullOrEmpty()){
                    binding?.searchProgressIndicator?.bringToFront()
                    binding?.searchProgressIndicator?.show()
                    binding?.repositories?.isVisible = false
                    //always back to the first item after list items update
                    repoAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            super.onItemRangeInserted(positionStart, itemCount)
                            binding?.repositories?.scrollToPosition(0)
                        }
                    })
                    viewModel?.getSearchResult(text.toString())
                    viewModel?.repoListLiveData?.observe(viewLifecycleOwner) { list ->
                        if (repoList != list && !list.isNullOrEmpty()) {
                            repoList = list
                            repoAdapter.submitList(repoList)
                            binding?.searchProgressIndicator?.hide()
                            binding?.repositories?.isVisible = true
                        }
                    }
                    viewModel?.errorLiveData?.observe(viewLifecycleOwner) {
                        if(it != null && it != errorBody){
                            errorBody = it
                            AlertDialog.Builder(context).setMessage(it?.message.plus("\n").plus(it?.documentation_url)).setTitle("Error").setPositiveButton("OK", null).show()
                            binding?.searchProgressIndicator?.hide()
                        }
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
            view?.findNavController()
                ?.navigate(R.id.action_repoSearchFragment_to_repoDetailFragment, bundle)
        })

        binding?.lifecycleOwner = this
        binding?.repositories?.layoutManager = layoutManager
        binding?.repositories?.adapter = repoAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}