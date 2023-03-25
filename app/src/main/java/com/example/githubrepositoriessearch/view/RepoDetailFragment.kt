package com.example.githubrepositoriessearch.view

import android.content.Context
import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.githubrepositoriessearch.databinding.FragmentRepoDetailBinding
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin;
import org.commonmark.node.Node


class RepoDetailFragment : Fragment() {
    private var _binding: FragmentRepoDetailBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        _binding = FragmentRepoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repoFullName : String? = arguments?.getString("repo")
        binding?.apply {
            binding?.lifecycleOwner = this.lifecycleOwner
            binding?.viewModel = sharedViewModel
            val paths : List<String>? = repoFullName?.split(" ")
            if (paths != null) {
                viewModel?.getRepository(paths[0], paths[1])
            }
            viewModel?.repoLiveData?.observe(viewLifecycleOwner) { repo ->
                binding?.repo = repo
                if(repo.has_issues){
                    binding.btnRepoIssues.visibility = View.VISIBLE
                }
                if(repo.has_projects){
                    binding.btnRepoProjects.visibility = View.VISIBLE
                }
                if(repo.has_discussions){
                    binding.btnRepoDiscussions.visibility = View.VISIBLE
                }
                viewModel?.getReadme(repo.owner.login, repo.name)
            }
            viewModel?.repoReadmeLiveData?.observe(viewLifecycleOwner){readme ->
                binding?.repoReadmeFilename?.text = readme.name
                context?.let {setReadme(binding?.repoReadme!!, readme.getContent())}
            }
        }
    }

    private fun setReadme(view: TextView, content: String) {
        val markwon:Markwon = Markwon.builder(requireContext())
            //TODO image cannot be loaded
            .usePlugin(GlideImagesPlugin.create(Glide.with(requireContext())))
            .usePlugin(TablePlugin.create(requireContext()))
            .usePlugin(HtmlPlugin.create())
            .build()
        markwon.setMarkdown(view, content)
    }

}