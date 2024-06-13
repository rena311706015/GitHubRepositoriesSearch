package repositories.search.model.response

import repositories.search.model.Commit
import repositories.search.model.User

class CommitResponse(
    val commit: Commit,
    val author: User,
)