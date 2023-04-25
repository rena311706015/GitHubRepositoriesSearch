package com.example.githubrepositoriessearch.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.adapter.CommitsAdapter
import com.example.githubrepositoriessearch.databinding.FragmentRepoCommitBinding
import com.example.githubrepositoriessearch.model.CommitResult
import com.example.githubrepositoriessearch.model.ErrorBody
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class RepoCommitFragment : Fragment() {
    private var _binding: FragmentRepoCommitBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var commitsAdapter: CommitsAdapter
    private lateinit var commitList: List<CommitResult>
    private var errorBody: ErrorBody = ErrorBody()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRepoCommitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            activity?.topAppBar?.title = getString(R.string.commits)
            binding.lifecycleOwner = this.lifecycleOwner
            binding.viewModel = sharedViewModel
            prepareRecyclerView()
            val repo = viewModel?.selectedRepo?.value
            val branch = viewModel?.selectedBranch?.value
            if (repo != null && branch != null) {
                viewModel?.getCommits(repo.owner.login, repo.name, branch.name)
            }
            viewModel?.repoCommitListLiveData?.observe(viewLifecycleOwner) { list ->
                commitList = list
                commitsAdapter.submitList(commitList)
            }
            viewModel?.errorLiveData?.observe(viewLifecycleOwner) {
                if (it != null && it != errorBody) {
                    errorBody = it
                    AlertDialog.Builder(context)
                        .setMessage(it.message.plus("\n").plus(it.documentation_url))
                        .setTitle("Error").setPositiveButton("OK", null).show()
                }
            }
        }
    }

    private fun prepareRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        commitList = emptyList()
        commitsAdapter = CommitsAdapter(CommitsAdapter.OnClickListener {})
        binding.lifecycleOwner = this
        binding.commits?.layoutManager = layoutManager
        binding.commits?.adapter = commitsAdapter
    }
}