package repositories.search.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import org.koin.androidx.viewmodel.ext.android.viewModel
import repositories.search.MainActivity
import repositories.search.R
import repositories.search.commons.ColorMapper
import repositories.search.commons.ImageLoader
import repositories.search.databinding.FragmentRepoDetailBinding
import repositories.search.model.Readme
import repositories.search.model.Repository
import repositories.search.view.file.RepoContentListFragment

class RepoDetailFragment : Fragment() {

    companion object {
        private const val ARG_OWNER_NAME = "ARG_OWNER_NAME"
        private const val ARG_REPO_NAME = "ARG_REPO_NAME"
        private const val ARG_BRANCH = "ARG_BRANCH"
        private const val ARG_RESULT = "ARG_RESULT"
        private const val ARG_INPUT = "ARG_INPUT"
    }

    private var binding: FragmentRepoDetailBinding? = null
    private val viewModel: RepoDetailViewModel by viewModel()

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
        binding = FragmentRepoDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
        setFragmentResultListener(ARG_RESULT) { _, bundle ->
            val targetBranch = bundle.getString(ARG_BRANCH) ?: return@setFragmentResultListener
            binding?.btnRepoBranch?.text = targetBranch
            view.let {
                Snackbar.make(
                    it,
                    getString(R.string.toast_notify_branch_changed_message, targetBranch),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupViews() {
        (activity as MainActivity).supportActionBar?.title = repoName

        binding?.progressIndicator?.isVisible = true
        binding?.scrollView?.isVisible = false
        val navController = view?.findNavController()
        binding?.btnRepoBranch?.setOnClickListener {
            val bundle = bundleOf(ARG_OWNER_NAME to ownerName, ARG_REPO_NAME to repoName)
            navController?.navigate(
                R.id.action_repoDetailFragment_to_repoBranchListFragment,
                bundle
            )
        }
        binding?.btnRepoBrowseCode?.setOnClickListener {
            val currentBranch = binding?.btnRepoBranch?.text.toString()
            val bundle = bundleOf(
                ARG_INPUT to RepoContentListFragment.Input(
                    ownerName = ownerName,
                    repoName = repoName,
                    branch = currentBranch
                )
            )
            navController?.navigate(
                R.id.action_repoDetailFragment_to_repoContentListFragment,
                bundle
            )

        }
        binding?.btnRepoCommits?.setOnClickListener {
            val bundle = bundleOf(ARG_OWNER_NAME to ownerName, ARG_REPO_NAME to repoName)
            navController?.navigate(
                R.id.action_repoDetailFragment_to_repoCommitListFragment,
                bundle
            )
        }
    }

    private fun setupViewModel() {
        viewModel.getRepository(ownerName, repoName)
        viewModel.getReadme(ownerName, repoName)
        viewModel.repository.observe(viewLifecycleOwner, ::onRepositoryChanged)
        viewModel.readme.observe(viewLifecycleOwner, ::onReadmeChanged)
    }

    private fun onRepositoryChanged(repository: Repository) {
        binding?.apply {
            ImageLoader.Builder(requireContext(), repository.owner.avatar_url).into(ownerAvatar)
            ownerName.text = repository.owner.login
            repoName.text = repository.name
            repoDescription.text = repository.description
            repoStar.text = repository.getStar()
            repoLanguage.text = repository.language
            if (btnRepoBranch.text.isEmpty()) btnRepoBranch.text = repository.default_branch
            if (repository.language != null) {
                val drawables = repoLanguage.compoundDrawables
                val drawableWithLanguageColor =
                    ContextCompat.getDrawable(requireContext(), R.drawable.round_circle)?.apply {
                        setTint(ColorMapper.getColorForLanguage(repository.language))
                    }
                repoLanguage.setCompoundDrawablesWithIntrinsicBounds(
                    drawableWithLanguageColor,
                    drawables[1],
                    drawables[2],
                    drawables[3]
                )
            } else repoLanguage.isVisible = false
            scrollView.isVisible = true
            progressIndicator.isVisible = false
        }
    }

    private fun onReadmeChanged(readme: Readme) {
        binding?.apply {
            repoReadmeFilename.text = readme.name
            setReadme(repoReadme, readme.getContent())
        }
    }

    private fun setReadme(view: TextView, content: String) {
        val markwon: Markwon = Markwon.builder(requireContext())
            .usePlugin(GlideImagesPlugin.create(Glide.with(requireContext())))
            .usePlugin(TablePlugin.create(requireContext()))
            .usePlugin(HtmlPlugin.create())
            .build()
        markwon.setMarkdown(view, content)
    }
}