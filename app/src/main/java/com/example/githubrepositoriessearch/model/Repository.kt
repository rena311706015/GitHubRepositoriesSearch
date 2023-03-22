package com.example.githubrepositoriessearch.model

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Repository(
    val id: Int,
    val full_name: String,
    val name: String,
    val owner: Owner,
    private val updated_at: String,
    val description: String,
    val language: String,
    private val stargazers_count: Int,
)
{
    fun getStar(): String {
        return if( stargazers_count >= 1000){
            val df = DecimalFormat("#.#")
            df.format(stargazers_count/1000)
        }else{
            stargazers_count.toString()
        }
    }
    fun getUpdateAt(): String {
        val outputFormat = SimpleDateFormat("MMM d, y")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        val date = inputFormat.parse(updated_at);
        return "Updated on "+outputFormat.format(date)
    }
}