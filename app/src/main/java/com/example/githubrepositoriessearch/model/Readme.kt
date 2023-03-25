package com.example.githubrepositoriessearch.model

import android.content.Context
import android.text.Spanned
import android.util.Base64
import android.widget.TextView
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import io.noties.markwon.Markwon
import org.commonmark.node.Node

class Readme (
    var name: String = "",
    private var content: String = "",
){
    fun getContent(): String {
        return String(Base64.decode(content, Base64.DEFAULT))
    }
}