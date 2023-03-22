package com.example.githubrepositoriessearch.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.databinding.FragmentRepoDetailBinding
import com.example.githubrepositoriessearch.databinding.FragmentRepoSearchBinding
import com.example.githubrepositoriessearch.viewmodel.MainViewModel

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
            binding?.viewModel = sharedViewModel
            val paths : List<String>? = repoFullName?.split(" ")
            if (paths != null) {
                viewModel?.getRepository(paths[0], paths[1])
            }
            viewModel?.repoLiveData?.observe(viewLifecycleOwner) { repo ->
                Log.e("repolivedata","observe()")
                binding?.repo = repo
            }
        }
    }
}