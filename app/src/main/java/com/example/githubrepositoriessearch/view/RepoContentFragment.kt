package com.example.githubrepositoriessearch.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepositoriessearch.R
import com.example.githubrepositoriessearch.adapter.ContentsAdapter
import com.example.githubrepositoriessearch.databinding.FragmentRepoContentBinding
import com.example.githubrepositoriessearch.model.Content
import com.example.githubrepositoriessearch.model.ErrorBody
import com.example.githubrepositoriessearch.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class RepoContentFragment : Fragment() {
    private var binding: FragmentRepoContentBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var contentsAdapter: ContentsAdapter
    private lateinit var contentList: List<Content>
    private var errorBody: ErrorBody = ErrorBody()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepoContentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            activity?.topAppBar?.title = getString(R.string.contents)
            binding?.lifecycleOwner = this.lifecycleOwner
            binding?.viewModel = sharedViewModel
            prepareRecyclerView()
            viewModel?.selectedContent?.observe(viewLifecycleOwner) {
                activity?.topAppBar?.title =
                    if (!it.path.isNullOrEmpty()) it.path else getString(R.string.contents)
            }
            viewModel?.repoContentListLiveData?.observe(viewLifecycleOwner) { list ->
                if (list != null) {
                    if (contentList.isNullOrEmpty()) {
                        contentList = list.sortedBy { it.type }
                        contentsAdapter.submitList(contentList)
                    } else if (list != contentList) { //TODO重複呼叫
                        var manager = parentFragmentManager
                        val transaction = manager.beginTransaction()
                        val fragment = RepoContentFragment()
                        transaction.add(R.id.nav_host_fragment, fragment, "content+")
                        transaction.hide(fragment)
                        transaction.addToBackStack("content")
                        transaction.commit()
                    }
                }
            }
            viewModel?.errorLiveData?.observe(viewLifecycleOwner) {
                if (it != null && it != errorBody) {
                    errorBody = it
                    AlertDialog.Builder(context)
                        .setMessage(it.message.plus("\n").plus(it.documentation_url))
                        .setTitle("Error").setPositiveButton("OK", null).show()
                }
            }
        }
    }

    private fun prepareRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        contentList = if (binding?.viewModel?.repoContentListLiveData?.value.isNullOrEmpty()) {
            emptyList()
        } else {
            binding?.viewModel?.repoContentListLiveData!!.value!!
        }
        contentsAdapter = ContentsAdapter(ContentsAdapter.OnClickListener { content ->
            binding?.viewModel?.openItem(content)
            //TODO 開啟多層資料夾後返回會回到detail而非上一層資料夾(可能需要開新的fragment)
            if (content.type == "dir") {
                val repo = binding?.viewModel?.selectedRepo?.value
                val branch = binding?.viewModel?.selectedBranch?.value
                if (repo != null && branch != null) {
                    binding?.viewModel?.getContents(
                        repo.owner.login,
                        repo.name,
                        content.path,
                        branch.name
                    )
                }
            } else {
                val bundle = bundleOf("fileName" to content.name, "fileUrl" to content.download_url)
                view?.findNavController()
                    ?.navigate(R.id.action_repoContentFragment_to_repoFileFragment, bundle)
            }
        })
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.contents?.layoutManager = layoutManager
        binding?.contents?.adapter = contentsAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.viewModel?.repoContentListLiveData!!.value = null
        binding = null
    }
}