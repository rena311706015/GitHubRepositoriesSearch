package com.example.githubrepositoriessearch.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Repository (
    val id: Int = 0,
    val full_name: String = "",
    val name: String = "",
    val description: String = "",
    val language: String = "",
    val owner: Owner = Owner(),
    private val updated_at: String = "",
    private val stargazers_count: Int = 0,

    val has_issues : Boolean,
    val has_projects : Boolean,
    val has_discussions : Boolean,
    val issues_url : String,
    val pulls_url : String,
    val releases_url : String,
    val contributors_url : String,

){

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