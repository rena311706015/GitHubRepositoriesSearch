package com.example.githubrepositoriessearch.view

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.databinding.FragmentRepoDetailBinding
import com.example.githubrepositoriessearch.model.ErrorBody
import com.example.githubrepositoriessearch.model.Repository
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.activity_main.*


class RepoDetailFragment : Fragment() {
    private var _binding: FragmentRepoDetailBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainViewModel by activityViewModels()
    private val fm: FragmentManager? = fragmentManager
    private var errorBody: ErrorBody = ErrorBody()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRepoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.topAppBar?.visibility = View.VISIBLE
        val repoFullName: String? = arguments?.getString("repo")
        val repoBranch: String? = arguments?.getString("branch")
        binding?.apply {
            binding?.lifecycleOwner = this.lifecycleOwner
            binding?.viewModel = sharedViewModel
            val paths: List<String>? = repoFullName?.split(" ")
            if (paths != null) {
                viewModel?.getRepository(paths[0], paths[1])
            }
            binding?.btnRepoBranch?.setOnClickListener {
                val bundle = bundleOf("repo" to (repo?.owner?.login).plus(" ").plus(repo?.name))
                view?.findNavController()
                    ?.navigate(R.id.action_repoDetailFragment_to_repoChooseBranchFragment, bundle)
            }
            binding?.btnRepoCommits?.setOnClickListener {
                val bundle = bundleOf(
                    "repo" to (repo?.owner?.login).plus(" ").plus(repo?.name),
                    "branch" to binding.btnRepoBranch.text
                )
                view?.findNavController()
                    ?.navigate(R.id.action_repoDetailFragment_to_repoCommitFragment, bundle)
            }
            binding?.btnRepoBrowseCode?.setOnClickListener {
                val bundle = bundleOf(
                    "repo" to (repo?.owner?.login).plus(" ").plus(repo?.name),
                    "branch" to binding.btnRepoBranch.text
                )
                view?.findNavController()
                    ?.navigate(R.id.action_repoDetailFragment_to_repoContentFragment, bundle)
            }
            viewModel?.repoLiveData?.observe(viewLifecycleOwner) { repo ->
                this.repo = repo
                if (repo != null) {
                    activity?.topAppBar?.title = repo.name
                    binding?.repo = repo
                    if (repoBranch != null) {
                        binding?.btnRepoBranch?.text = repoBranch
                    } else {
                        binding?.btnRepoBranch?.text = repo?.default_branch
                    }
                    viewModel?.getReadme(repo.owner.login, repo.name)
                } else {
                    Log.e("repo", "isNull")
                }
            }
            viewModel?.repoReadmeLiveData?.observe(viewLifecycleOwner) { readme ->
                binding?.repoReadmeFilename?.text = readme?.name
                context?.let { setReadme(binding?.repoReadme!!, readme.getContent()!!) }
            }
            viewModel?.errorLiveData?.observe(viewLifecycleOwner) {
                if(it != null && it != errorBody){
                    errorBody = it
                    AlertDialog.Builder(context).setMessage(it?.message.plus("\n").plus(it?.documentation_url)).setTitle("Error").setPositiveButton("OK", null).show()
                    binding?.scrollView?.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "Destroyed")
        fm?.popBackStack()
        if (fm != null) {
            for (entry in 0 until fm.backStackEntryCount) {
                Log.e(TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).id)
            }
        }
    }

    private fun setReadme(view: TextView, content: String) {
        val markwon: Markwon = Markwon.builder(requireContext())
            //TODO image cannot be loaded
            .usePlugin(GlideImagesPlugin.create(Glide.with(requireContext())))
            .usePlugin(TablePlugin.create(requireContext()))
            .usePlugin(HtmlPlugin.create())
            .build()
        markwon.setMarkdown(view, content)
    }

}