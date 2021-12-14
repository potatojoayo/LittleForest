package com.benhan.bluegreen.utill
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.asynclayoutinflater.view.AsyncLayoutInflater

class AsyncFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
) {
    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

    private var isInflated = false
    private var pendingActions: MutableList<AsyncFrameLayout.() -> Unit> = ArrayList()

    fun inflateAsync(layoutResId: Int) {
        AsyncLayoutInflater(context).inflate(layoutResId, this) { view, _, _ ->
            addView(view)
            isInflated = true
            pendingActions.forEach { action -> action() }
            pendingActions.clear()
        }
    }

    fun invokeWhenInflated(action: AsyncFrameLayout.() -> Unit) {
        if (isInflated) {
            action()
        } else {
            pendingActions.add(action)
        }
    }
}