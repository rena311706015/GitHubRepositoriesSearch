package com.example.githubrepositoriessearch.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.databinding.FragmentRepoDetailBinding
import com.example.githubrepositoriessearch.model.ErrorBody
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.activity_main.*


class RepoDetailFragment : Fragment() {
    private var binding: FragmentRepoDetailBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()
    private var errorBody: ErrorBody = ErrorBody()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentRepoDetailBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.topAppBar?.visibility = View.VISIBLE

        binding?.apply {
            var repo = viewModel?.selectedRepo?.value
            var branch = viewModel?.selectedBranch?.value

            binding?.lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            binding?.btnRepoBranch?.setOnClickListener {
                view.findNavController()
                    ?.navigate(R.id.action_repoDetailFragment_to_repoChooseBranchFragment)
            }
            binding?.btnRepoCommits?.setOnClickListener {
                view.findNavController()
                    ?.navigate(R.id.action_repoDetailFragment_to_repoCommitFragment)
            }
            binding?.btnRepoBrowseCode?.setOnClickListener {
                viewModel?.getContents(
                    repo?.owner?.login!!, repo?.name!!, "",
                    binding?.btnRepoBranch?.text as String
                )
                viewModel?.repoContentListLiveData?.observe(viewLifecycleOwner) {
                    if (!it.isNullOrEmpty()) {
                        view.findNavController()
                            ?.navigate(R.id.action_repoDetailFragment_to_repoContentFragment)
                    }
                }
            }

            viewModel?.repoLiveData?.observe(viewLifecycleOwner) { it ->
                if (it != null) {
                    activity?.topAppBar?.title = it.name
                    repo = it
                    binding?.repo = it
                    binding?.btnRepoBranch?.text =
                        if (branch != null) branch.name else it?.default_branch
                } else {
                    Log.e("Detail", "isNull")
                }
            }
            viewModel?.selectedBranch?.observe(viewLifecycleOwner) { branch ->
                binding?.btnRepoBranch?.text = branch.name
                if (arguments?.getString("from") == "chooseBranch") {
                    arguments?.clear()
                    Snackbar.make(view, "Change branch to ".plus(branch.name), Snackbar.LENGTH_LONG)
                        .show()
                }
            }
            viewModel?.repoReadmeLiveData?.observe(viewLifecycleOwner) { readme ->
                if (readme != null) {
                    binding?.repoReadmeFilename?.text = readme.name
                    context?.let { setReadme(binding?.repoReadme!!, readme.getContent()) }
                }
            }
            viewModel?.errorLiveData?.observe(viewLifecycleOwner) {
                if (it != null && it != errorBody) {
                    errorBody = it
                    AlertDialog.Builder(context)
                        .setMessage(it.message.plus("\n").plus(it.documentation_url))
                        .setTitle("Error").setPositiveButton("OK", null).show()
                    binding?.scrollView?.visibility = View.GONE
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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