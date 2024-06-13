package repositories.search.view.commit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import repositories.search.MainActivity
import repositories.search.R
import repositories.search.databinding.FragmentRepoCommitBinding

class RepoCommitListFragment : Fragment() {

    companion object {
        private const val ARG_OWNER_NAME = "ARG_OWNER_NAME"
        private const val ARG_REPO_NAME = "ARG_REPO_NAME"
    }

    private var binding: FragmentRepoCommitBinding? = null
    private val viewModel: RepoCommitListViewModel by viewModel()
    private val adapter = RepoCommitListAdapter()

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
        binding = FragmentRepoCommitBinding.inflate(inflater, container, false)
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
        (activity as MainActivity).supportActionBar?.title = getString(R.string.repo_commits)
        binding?.commits?.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel.getCommits(ownerName, repoName)
        viewModel.commits.observe(viewLifecycleOwner) { adapter.items = it }
    }
}