package repositories.search.model

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class Repository(
    private val updated_at: String = "",
    private val stargazers_count: Int = 0,
    val id: Int = 0,
    val full_name: String = "",
    val name: String = "",
    val description: String = "",
    val language: String? = "",
    val owner: User = User(),
    val default_branch: String = "",
    var readme: Readme? = null,
) {

    fun getStar(): String {
        return if (stargazers_count >= 1000) {
            val df = DecimalFormat("#.#k")
            df.format(stargazers_count / 1000.0)
        } else {
            stargazers_count.toString()
        }
    }

    fun getUpdateAt(): String {
        val outputFormat = SimpleDateFormat("MMM d, y")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = inputFormat.parse(updated_at)
        return "Updated on " + outputFormat.format(date)
    }
}