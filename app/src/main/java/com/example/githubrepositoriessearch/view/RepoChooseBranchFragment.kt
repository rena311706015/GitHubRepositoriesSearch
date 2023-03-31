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
import com.example.githubrepositoriessearch.adapter.BranchesAdapter
import com.example.githubrepositoriessearch.databinding.FragmentRepoChooseBranchBinding
import com.example.githubrepositoriessearch.model.Branch
import com.example.githubrepositoriessearch.model.ErrorBody
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class RepoChooseBranchFragment : Fragment() {
    private var _binding: FragmentRepoChooseBranchBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var branchesAdapter: BranchesAdapter
    private lateinit var branchList: List<Branch>
    private var errorBody: ErrorBody = ErrorBody()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRepoChooseBranchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repoFullName: String? = arguments?.getString("repo")
        binding?.apply {
            activity?.topAppBar?.title = getString(R.string.choose_branch)
            binding?.lifecycleOwner = this.lifecycleOwner
            binding?.viewModel = sharedViewModel
            prepareRecyclerView()
            val paths: List<String>? = repoFullName?.split(" ")
            if (paths != null) {
                viewModel?.getBranches(paths[0], paths[1])
            }
        }
        binding.viewModel?.repoBranchListLiveData?.observe(viewLifecycleOwner) { list ->
            branchList = list
            branchesAdapter.submitList(branchList)
        }
        binding.viewModel?.errorLiveData?.observe(viewLifecycleOwner) {
            if(it != null && it != errorBody){
                errorBody = it
                AlertDialog.Builder(context).setMessage(it?.message.plus("\n").plus(it?.documentation_url)).setTitle("Error").setPositiveButton("OK", null).show()
            }
        }
    }

    private fun prepareRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        branchList = emptyList()
        branchesAdapter = BranchesAdapter(BranchesAdapter.OnClickListener { branch ->
            val bundle = bundleOf("branch" to branch.name)
            view?.findNavController()
                ?.navigate(R.id.action_repoChooseBranchFragment_to_repoDetailFragment, bundle)
        })

        binding?.lifecycleOwner = this
        binding?.branches?.layoutManager = layoutManager
        binding?.branches?.adapter = branchesAdapter
    }
}