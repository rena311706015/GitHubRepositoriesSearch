package com.example.githubrepositoriessearch.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.githubrepositoriessearch.MainActivity
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.adapter.RepositoriesAdapter
import com.example.githubrepositoriessearch.databinding.FragmentRepoSearchBinding
import com.example.githubrepositoriessearch.model.Branch
import com.example.githubrepositoriessearch.model.ErrorBody
import com.example.githubrepositoriessearch.model.Repository
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class RepoSearchFragment : Fragment() {

    private var binding: FragmentRepoSearchBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var repoAdapter: RepositoriesAdapter
    private lateinit var repoList: List<Repository>
    private var errorBody: ErrorBody = ErrorBody()
    private lateinit var alert : AlertDialog
    private var isComeback: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentRepoSearchBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.e("Search", "onBackPressed")
                    alert = AlertDialog.Builder(context).create()
                    alert.setTitle("Exit?")
                    alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Stay") { _, _ ->

                    }
                    alert.setButton(DialogInterface.BUTTON_POSITIVE, "Exit") { _, _ ->
                        activity?.finish()
                    }
                    alert.show()
                    isEnabled = true
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.topAppBar?.visibility = View.GONE
        binding!!.apply {
            binding?.lifecycleOwner = viewLifecycleOwner
            binding?.viewModel = sharedViewModel
            prepareRecyclerView()
            val searchEditText = binding?.searchEditText
            searchEditText?.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrEmpty()) {
                    binding?.searchProgressIndicator?.bringToFront()
                    binding?.searchProgressIndicator?.show()
                    binding?.repositories?.isVisible = false
                    //always back to the first item after list items update
                    repoAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            super.onItemRangeInserted(positionStart, itemCount)
                            if(!isComeback){
                                binding?.repositories?.scrollToPosition(0)
                            }
                        }
                    })
                    viewModel?.getSearchResult(text.toString())
                    viewModel?.repoListLiveData?.observe(viewLifecycleOwner) { list ->
                        if(!isComeback){
                            if (repoList != list && !list.isNullOrEmpty()) {
                                repoList = list
                                repoAdapter.submitList(repoList)
                                binding?.searchProgressIndicator?.hide()
                                binding?.repositories?.isVisible = true
                            }
                        }else{
                            isComeback = false
                        }
                    }
                    viewModel?.errorLiveData?.observe(viewLifecycleOwner) {
                        if (it != null && it != errorBody) {
                            errorBody = it
                            if(!::alert.isInitialized){
                                alert = AlertDialog.Builder(context).create()
                            }
                            if (!alert.isShowing) {
                                alert.setTitle("Error")
                                alert.setMessage(it.message.plus("\n").plus(it.documentation_url))
                                alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ -> }
                                alert.show()
                            }

                            binding?.searchProgressIndicator?.hide()
                        }
                    }
                }else{
                    binding?.repositories?.isVisible = false
                }
            }
        }
    }

    private fun prepareRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        repoList = emptyList()
        repoAdapter = RepositoriesAdapter(RepositoriesAdapter.OnClickListener {
            binding?.repositories?.isVisible = false
            binding?.searchProgressIndicator?.show()
            binding?.viewModel?.getRepository(it.owner.login, it.name)
            binding?.viewModel?.repoLiveData?.observe(viewLifecycleOwner) { repo ->
                binding?.viewModel?.openItem(repo)
                binding?.viewModel?.openItem(Branch(repo.default_branch))
                if ((it.full_name == repo.full_name) && repo != null) {
                    binding?.viewModel?.getReadme(repo.owner.login, repo.name)
                    binding?.viewModel?.repoReadmeLiveData?.observe(viewLifecycleOwner) { readme ->
                        if (readme.name == "" || readme.getContent() == binding?.viewModel!!.selectedRepo.value?.readme?.getContent()) {
                            binding?.searchProgressIndicator?.hide()
                            isComeback = true
                            view?.findNavController()
                                ?.navigate(R.id.action_repoSearchFragment_to_repoDetailFragment)
                        }
                    }
                }
            }
        })
        binding?.repositories?.layoutManager = layoutManager
        binding?.repositories?.adapter = repoAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}