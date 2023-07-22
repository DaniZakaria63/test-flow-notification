package com.example.testapplication.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

/*to draw header
* circle and wave with 45 degrees
* */
class HeaderBackgroundView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private val path: Path = Path()
    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#dadada")
    }
    private var image : Drawable? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCurvedLine(canvas)
        drawImage(canvas)
    }

    fun putImage(image: Drawable){
        this.image = image
        postInvalidate()
    }

    private fun drawImage(canvas: Canvas?){
        this.image.let {

        }
    }

    private fun drawCurvedLine(canvas: Canvas?) {
        /*from bottom left
        * to center with left arch
        * to more right with right arch
        * to top right
        * to bottom right (line)
        * to bottom left (line)
        * method: quadTo(max curve width, max curve height, end point width, end point height)
        * */

        path.moveTo(0f, 1f * height) // bottom left (0,10)
        path.quadTo(0.1f * width, 0.7f * height, 0.55f * width, 0.6f * height) // (2,7) (5.5, 5.5)
        path.quadTo(0.8f * width, 0.5f * height, 0.8f * width, 0.3f * height) // (8,5) (9,3)
        path.quadTo(0.8f * width, 0.1f * height, 1f * width, 0f) // (9.5,0.8) (10,0)
        path.lineTo(1f * width, 1f * height) // (10,10)
        path.lineTo(0f, 1f * height) // same as the first position (0, 10)

        canvas?.drawPath(path, pathPaint)
    }
}