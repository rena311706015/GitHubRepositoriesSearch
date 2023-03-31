package com.example.githubrepositoriessearch.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.adapter.ContentsAdapter
import com.example.githubrepositoriessearch.databinding.FragmentRepoContentBinding
import com.example.githubrepositoriessearch.model.Content
import com.example.githubrepositoriessearch.model.ErrorBody
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class RepoContentFragment : Fragment() {
    private var _binding: FragmentRepoContentBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var contentsAdapter: ContentsAdapter
    private lateinit var contentList: List<Content>
    private lateinit var owner: String
    private lateinit var repoName: String
    private lateinit var repoBranch: String
    private var errorBody: ErrorBody = ErrorBody()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRepoContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repoFullName: String? = arguments?.getString("repo")
        repoBranch = arguments?.getString("branch").toString()

        binding?.apply {
            activity?.topAppBar?.title = getString(R.string.contents)
            binding?.lifecycleOwner = this.lifecycleOwner
            binding?.viewModel = sharedViewModel
            prepareRecyclerView()
            val paths: List<String>? = repoFullName?.split(" ")
            owner = paths?.get(0).toString()
            repoName = paths?.get(1).toString()

            if (paths != null && repoBranch != null) {
                viewModel?.getContents(owner, repoName, "", repoBranch)
            }
        }
        binding.viewModel?.repoContentListLiveData?.observe(viewLifecycleOwner) { list ->
            contentList = list.sortedBy { it.type }
            contentsAdapter.submitList(contentList)
        }
        binding.viewModel?.errorLiveData?.observe(viewLifecycleOwner) {
            if (it != null && it != errorBody) {
                errorBody = it
                AlertDialog.Builder(context)
                    .setMessage(it?.message.plus("\n").plus(it?.documentation_url))
                    .setTitle("Error").setPositiveButton("OK", null).show()
            }
        }
    }

    private fun prepareRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        contentList = emptyList()
        contentsAdapter = ContentsAdapter(ContentsAdapter.OnClickListener { content ->
            if (content.type == "dir") {
                binding.viewModel?.getContents(owner, repoName, content.path, repoBranch)
            } else {
                val bundle = bundleOf("fileName" to content.name, "fileUrl" to content.download_url)
                view?.findNavController()
                    ?.navigate(R.id.action_repoContentFragment_to_repoFileFragment, bundle)
            }
        })
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.contents?.layoutManager = layoutManager
        binding?.contents?.adapter = contentsAdapter
    }
}