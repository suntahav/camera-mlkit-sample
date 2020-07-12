package com.sandstorm.camera_mlkit_sample.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import com.sandstorm.camera_mlkit_sample.utils.TracePath
import kotlin.math.abs

class PaintView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val DEFAULT_BRUSH_SIZE = 20F
    private val DEFAULT_COLOR: Int = Color.WHITE
    private val DEFAULT_BG_COLOR = Color.BLACK
    private val TOUCH_TOLERANCE = 4F

    private lateinit var mPath: Path
    private var mPaint: Paint
    private val path = arrayListOf<TracePath>()
    private var mBlur: MaskFilter
     var mBitmap: Bitmap
    private var mCanvas: Canvas

    private var currentColor: Int
    private var strokeWidth: Float
    private var blur: Boolean
    private var bgColor = DEFAULT_BG_COLOR
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)


    private var mX = 0f
    private var mY = 0f

    init {
        mPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = DEFAULT_COLOR
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            xfermode = null
            alpha = 0xFF
            mBlur = BlurMaskFilter(5.0F, BlurMaskFilter.Blur.NORMAL)
        }

        val height = context.resources.displayMetrics.widthPixels
        val width = context.resources.displayMetrics.widthPixels
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)

        currentColor = DEFAULT_COLOR
        strokeWidth = DEFAULT_BRUSH_SIZE
        blur = false
    }

    fun normal() {
        blur = false
    }

    fun blur() {
        blur = true
    }

    fun clear() {
        bgColor = DEFAULT_BG_COLOR
        path.clear()
        normal()
        invalidate()
    }

    private fun pathStart(x: Float, y: Float) {
        mPath = Path()
        val tracePath = TracePath(DEFAULT_COLOR, true, 20F, mPath)
        path.add(tracePath)
        mPath.reset()
        mPath.moveTo(x,y)
        mX = x
        mY = y
    }

    private fun pathMove(x: Float, y: Float){
        val dx = abs(x-mX)
        val dy = abs(y-mY)

        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            mPath.quadTo(mX,mY,(mX+x)/2,(mY+y)/2)
            mX = x
            mY = y
        }
    }

    private fun pathStop(){
        mPath.lineTo(mX,mY)
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x != null && y != null) {
                    pathStart(x, y)
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (x != null && y != null) {
                    pathMove(x, y)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                pathStop()
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
        mCanvas.drawColor(bgColor)

        for (p in path) {
            mPaint.apply {
                color = p.color
                strokeWidth = p.strokeWidth
                maskFilter = null
            }

            if(p.blur) mPaint.maskFilter = mBlur

            mCanvas.drawPath(p.path,mPaint)

        }
        canvas?.drawBitmap(mBitmap, 0F, 0F, mBitmapPaint )
        canvas?.restore()
    }
}