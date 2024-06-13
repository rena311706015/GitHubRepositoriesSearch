package repositories.search.view.file

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.parcelize.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel
import repositories.search.MainActivity
import repositories.search.R
import repositories.search.databinding.FragmentRepoContentBinding
import repositories.search.model.Content

class RepoContentListFragment : Fragment() {

    companion object {
        private const val ARG_INPUT = "ARG_INPUT"
        private const val ARG_FILE_URL = "ARG_FILE_URL"
        private const val ARG_FILE_NAME = "ARG_FILE_NAME"

        fun newInstance(input: Input) = RepoContentListFragment().apply {
            arguments = bundleOf(ARG_INPUT to input)
        }
    }

    @Parcelize
    class Input(
        val ownerName: String,
        val repoName: String,
        val path: String? = null,
        val branch: String,
    ) : Parcelable

    private val input: Input by lazy {
        requireArguments().getParcelable(ARG_INPUT)!!
    }

    private var binding: FragmentRepoContentBinding? = null
    private val viewModel: RepoContentListViewModel by viewModel()
    private val adapter = RepoContentListAdapter(object : RepoContentListAdapter.Interaction {
        override fun onContentClicked(content: Content) {
            if (content.isDir()) {
                val fragment = newInstance(
                    Input(
                        ownerName = input.ownerName,
                        repoName = input.repoName,
                        path = content.path,
                        branch = input.branch
                    )
                )
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frameLayout, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            } else {
                val bundle =
                    bundleOf(ARG_FILE_URL to content.download_url, ARG_FILE_NAME to content.name)
                val navController = view?.findNavController()
                navController?.navigate(
                    R.id.action_repoContentListFragment_to_repoFileFragment,
                    bundle
                )
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepoContentBinding.inflate(inflater, container, false)
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
        (activity as MainActivity).supportActionBar?.title =
            input.path ?: getString(R.string.repo_files)
        binding?.contents?.adapter = adapter
    }

    private fun setupViewModel() {
        val path = input.path ?: ""
        viewModel.getContents(input.ownerName, input.repoName, path, input.branch)
        viewModel.contents.observe(viewLifecycleOwner) {
            adapter.items = it
        }
    }
}