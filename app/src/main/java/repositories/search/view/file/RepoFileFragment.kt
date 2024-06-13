package repositories.search.view.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import repositories.search.MainActivity
import repositories.search.databinding.FragmentRepoFileBinding

class RepoFileFragment : Fragment() {

    companion object {
        private const val ARG_FILE_URL = "ARG_FILE_URL"
        private const val ARG_FILE_NAME = "ARG_FILE_NAME"
    }

    private var _binding: FragmentRepoFileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepoFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fileUrl = arguments?.getString(ARG_FILE_URL) ?: return
        val fileName = arguments?.getString(ARG_FILE_NAME) ?: return

        binding.apply {
            (activity as MainActivity).supportActionBar?.title = fileName
            repoFileRawContent.loadUrl(fileUrl)
        }
    }
}