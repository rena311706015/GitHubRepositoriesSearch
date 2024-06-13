package repositories.search.model

import android.util.Base64

class Readme(
    var name: String = "",
    private var content: String = "",
) {
    //readme的content是用base64 encode的，所以要先decode
    fun getContent(): String {
        return String(Base64.decode(content, Base64.DEFAULT))
    }
}
