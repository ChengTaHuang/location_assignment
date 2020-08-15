package com.cartrack.assignment.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText

class TouchEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private var oriAlpha: Float = 0.0f
    private val tapAlpha = 0.65f

    init {
        oriAlpha = this.alpha
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                this.alpha = oriAlpha * tapAlpha
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                this.alpha = oriAlpha
            }
        }

        return super.onTouchEvent(event)
    }
}