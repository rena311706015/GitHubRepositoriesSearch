package repositories.search.view.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import repositories.search.MainActivity
import repositories.search.R
import repositories.search.databinding.FragmentRepoSearchBinding


class RepoSearchListFragment : Fragment() {

    companion object {
        private const val ARG_OWNER_NAME = "ARG_OWNER_NAME"
        private const val ARG_REPO_NAME = "ARG_REPO_NAME"
    }

    private var binding: FragmentRepoSearchBinding? = null
    private var adapter: RepoSearchListAdapter? = null
    private var currentItemCount = 0

    private val viewModel: RepoSearchListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepoSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    MaterialAlertDialogBuilder(context)
                        .setTitle(getString(R.string.dialog_exit_title))
                        .setMessage(getString(R.string.dialog_exit_message))
                        .setPositiveButton(getString(R.string.dialog_yes_button)) { _, _ -> activity?.finish() }
                        .setNegativeButton(getString(R.string.dialog_no_button)) { _, _ -> }
                        .show()
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupViews() {
        (activity as MainActivity).supportActionBar?.title = getString(R.string.app_name)
        binding?.apply {
            val layoutManager = LinearLayoutManager(context)
            repositories.layoutManager = layoutManager
            adapter = RepoSearchListAdapter(object : RepoSearchListAdapter.Interaction {
                override fun onRepoClicked(ownerName: String, repoName: String) {
                    val bundle = bundleOf(ARG_OWNER_NAME to ownerName, ARG_REPO_NAME to repoName)
                    val navController = view?.findNavController()
                    navController?.navigate(
                        R.id.action_repoSearchListFragment_to_repoDetailFragment,
                        bundle
                    )
                }
            })

            repositories.adapter = adapter
            repositories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastVisibleItemPosition == adapter!!.itemCount - 10 && currentItemCount != adapter!!.itemCount) {
                        val nextPage = lastVisibleItemPosition / 20 + 1
                        currentItemCount = adapter!!.itemCount
                        viewModel.getSearchResult(
                            viewModel.textState.value,
                            nextPage,
                            loadMore = true
                        )
                    }
                }
            })
            searchInputEditText.doAfterTextChanged { editable ->
                repositories.isVisible = false
                currentItemCount = 0
                val searchString = editable.toString()
                if (searchString.isNotEmpty()) {
                    searchProgressIndicator.isVisible = true
                    viewModel.textState.value = searchString
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel.repositories.observe(viewLifecycleOwner) {
            adapter?.items = it
            binding?.searchProgressIndicator?.isVisible = false
            binding?.noResultTextView?.isVisible = false
            binding?.repositories?.isVisible = true
        }
        viewModel.errorType.observe(viewLifecycleOwner) {
            binding?.searchProgressIndicator?.isVisible = false
            binding?.noResultTextView?.isVisible = it == RepoSearchListViewModel.ErrorType.EMPTY
            if (it == RepoSearchListViewModel.ErrorType.EMPTY) return@observe

            val title =
                getString(if (it == RepoSearchListViewModel.ErrorType.REACH_LIMIT) R.string.dialog_reach_limit_title else R.string.dialog_general_error_title)
            val message =
                getString(if (it == RepoSearchListViewModel.ErrorType.REACH_LIMIT) R.string.dialog_reach_limit_message else R.string.dialog_general_error_message)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.dialog_ok_button)) { _, _ -> }
                .show()
        }
    }
}