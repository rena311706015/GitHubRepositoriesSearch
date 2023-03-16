package com.example.githubrepositoriessearch.model

import java.text.DecimalFormat

class Repository(
    val id: Int,
    val full_name: String,
    val name: String,
    val owner: Owner,
    val url: String,
    val tags_url: String,
    private val updated_at: String,
    val description: String,
    val language: String,
    private val star: Int,
)
{
    fun getStar(): String {
        return if( star >= 1000){
            val df = DecimalFormat("#.#")
            df.format(star/1000)
        }else{
            star.toString()
        }
    }
    //TODO Correctly transfer "2022-12-30T17:58:47Z" -> Updated on Dec 30,2022
    fun getUpdateAt(): String {
//        val outputFormat = SimpleDateFormat("MMM d, y")
//        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
//        val date = inputFormat.parse(updated_at);
//        return "Updated on "+outputFormat.format(date)
        return "Updated on $updated_at"
    }
}