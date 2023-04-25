package com.example.githubrepositoriessearch.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.githubrepositoriessearch.databinding.FragmentRepoFileBinding
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class RepoFileFragment : Fragment() {
    private var _binding: FragmentRepoFileBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRepoFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fileName: String? = arguments?.getString("fileName")
        val fileUrl: String? = arguments?.getString("fileUrl")

        binding.apply {
            activity?.topAppBar?.title = fileName
            binding.lifecycleOwner = this.lifecycleOwner
            binding.viewModel = sharedViewModel
            if (fileUrl != null) {
                //TODO 不知道怎麼在app內瀏覽不同種的檔案所以先用webview顯示rawContent
                binding.repoFileRawContent?.loadUrl(fileUrl)
            }
        }
    }

}