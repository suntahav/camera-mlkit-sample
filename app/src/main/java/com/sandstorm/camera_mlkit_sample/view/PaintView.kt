package com.sandstorm.camera_mlkit_sample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PaintView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var DEFAULT_BRUSH_SIZE = 20
    var DEFAULT_COLOR = Color.RED
    var DEFAULT_BG_COLOR = Color.BLACK

    private lateinit var mPath: Path
    private fun pathStart(x : Float,y : Float){
        mPath = Path()

    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var x = event?.x
        var y = event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                invalidate()
            }
            else -> {
                return true
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.save()

        canvas?.restore()
    }
}