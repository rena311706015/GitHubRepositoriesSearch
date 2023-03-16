package com.example.githubrepositoriessearch.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.databinding.FragmentRepoDetailBinding
import com.example.githubrepositoriessearch.viewmodel.MainViewModel

class RepoDetailFragment : Fragment() {
    companion object {
        fun newInstance() =
            RepoDetailFragment()
    }
    private var binding: FragmentRepoDetailBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {

        val viewDataBinding: FragmentRepoDetailBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_repo_detail, container, false
        )

        return viewDataBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            binding = FragmentRepoDetailBinding.inflate(layoutInflater)
            binding?.viewModel = sharedViewModel
            viewModel?.getSearchResult("test")

        }
    }
}