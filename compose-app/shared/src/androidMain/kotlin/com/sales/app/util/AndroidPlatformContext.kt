package com.sales.app.util

import android.content.Context
import java.lang.ref.WeakReference

object AndroidPlatformContext {
    private var contextRef: WeakReference<Context>? = null

    fun set(context: Context) {
        this.contextRef = WeakReference(context)
    }

    fun get(): Context? {
        return contextRef?.get()
    }
}
