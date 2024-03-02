package com.example.expensetracker.services

import android.content.Context
import androidx.annotation.StringRes

interface ResourceResolver {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

class ResourceResolverImpl(private val context: Context): ResourceResolver {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}