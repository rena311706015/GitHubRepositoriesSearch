package repositories.search.view.branch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import repositories.search.MainActivity
import repositories.search.R
import repositories.search.databinding.FragmentRepoBranchBinding
import repositories.search.model.Branch

class RepoBranchListFragment : Fragment() {

    companion object {
        private const val ARG_OWNER_NAME = "ARG_OWNER_NAME"
        private const val ARG_REPO_NAME = "ARG_REPO_NAME"
        private const val ARG_BRANCH = "ARG_BRANCH"
        private const val ARG_RESULT = "ARG_RESULT"
    }

    private var binding: FragmentRepoBranchBinding? = null
    private val viewModel: RepoBranchListViewModel by viewModel()
    private val adapter = RepoBranchListAdapter(object : RepoBranchListAdapter.Interaction {
        override fun onBranchClicked(branch: Branch) {
            setFragmentResult(ARG_RESULT, bundleOf(ARG_BRANCH to branch.name))
            parentFragmentManager.popBackStack()
        }
    })

    private val ownerName: String by lazy {
        arguments?.getString(ARG_OWNER_NAME) ?: ""
    }
    private val repoName: String by lazy {
        arguments?.getString(ARG_REPO_NAME) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepoBranchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupViewModel()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun setupViews() {
        (activity as MainActivity).supportActionBar?.title = getString(R.string.choose_branch)
        binding?.branches?.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel.getBranches(ownerName, repoName)
        viewModel.branches.observe(viewLifecycleOwner) {
            adapter.items = it
        }
    }
}