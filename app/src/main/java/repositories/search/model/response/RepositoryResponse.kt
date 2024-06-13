package repositories.search.model.response

import repositories.search.model.Repository

class RepositoryResponse(
    val total_count: Int,
    val items: List<Repository>,
    val message: String,
    val documentation_url: String,
)