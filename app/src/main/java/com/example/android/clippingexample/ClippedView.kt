package com.example.android.clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

class ClippedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    private val path = Path()

    // Variables for dimensions for a clipping rectangle around the whole set of shapes.
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    // Variables for the inset of a rectangle and the offset of a small rectangle.
    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    // A variable for the radius of a circle. This is the circle that is drawn inside the rectangle.
    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    // An offset and a text size for text that is drawn inside the rectangle.
    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    // Coordinates for two columns.
    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    // Coordinates for each row, including the final row for the transformed text.
    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)

    // a variable for the y coordinates of an additional row.
    private val rejectRow = rowFour + rectInset + 2 * clipRectBottom

    // create and initialize a rectangle variable. RectF is a class that holds
    // rectangle coordinates in floating point.
    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Call a function for each shape you are drawing.
        drawBackAndUnclippedRectangle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawSkewedTextExample(canvas)
        drawTranslatedTextExample(canvas)
        drawQuickRejectExample(canvas)
    }

    private fun drawClippedRectangle(canvas: Canvas) {

        /**
         * The Canvas.clipRect(left, top, right, bottom) method reduces the region of the screen
         * that future draw operations can write to. It sets the clipping boundaries to be
         * the spatial intersection of the current clipping rectangle and
         * the rectangle passed into clipRect(). There are a lot of variants of
         * the clipRect() method that accept different forms of regions and allow
         * different operations on the clipping rectangle.
         * Check the linked documentation to learn more.
         * https://developer.android.com/reference/kotlin/android/graphics/Canvas#clipRect(kotlin.Float,%20kotlin.Float,%20kotlin.Float,%20kotlin.Float)
         */
        // Set the boundaries of the clipping rectangle for the whole shape.
        // Apply a clipping rectangle that constrains to drawing only the square.
        canvas.clipRect(
            clipRectLeft,
            clipRectTop,
            clipRectRight,
            clipRectBottom
        )

        // code to fill the canvas with white color.
        // Only the region inside the clipping rectangle will be filled!
        canvas.drawColor(Color.WHITE)

        // Change the color to red and draw a diagonal line inside the clipping rectangle.
        paint.color = Color.RED
        canvas.drawLine(
            clipRectLeft,
            clipRectTop,
            clipRectRight,
            clipRectBottom,
            paint
        )

        // Set the color to green and draw a circle inside the clipping rectangle.
        paint.color = Color.GREEN
        canvas.drawCircle(
            circleRadius,
            clipRectBottom - circleRadius,
            circleRadius,
            paint
        )

        // Set the color to blue and draw text aligned with the right edge
        // of the clipping rectangle. Use Canvas.drawText() to draw text.
        paint.color = Color.BLUE

        paint.textSize = textSize
        /*ContactsContract.CommonDataKinds.Note: The Paint.Align property specifies which side of the text to align to the origin
         (not which side of the origin the text goes, or where in the region it is aligned!).
         Aligning the right side of the text to the origin places it on the left of the origin.*/
        // Align the RIGHT side of the text with the origin.
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            context.getString(R.string.clipping),
            clipRectRight,
            textOffset,
            paint
        )
    }

    private fun drawBackAndUnclippedRectangle(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
        canvas.save()
        canvas.translate(columnOne, rowOne)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    //   The code does the following:
    //
    // - Save the canvas.
    // - Translate the origin of the canvas into open space to the first row, second column,
    //   to the right of the first rectangle.
    // - Apply two clipping rectangles. The DIFFERENCE operator subtracts the second rectangle
    //   from the first one.
    // - Call the drawClippedRectangle() method to draw the modified canvas.
    // - Restore the canvas state.
    private fun drawDifferenceClippingExample(canvas: Canvas) {
        canvas.save()
        // Move the origin to the right for the next rectangle.
        canvas.translate(columnTwo, rowOne)
        // Use the subtraction of two clipping rectangles to create a frame.
        canvas.clipRect(
            2 * rectInset,
            2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .DIFFERENCE) was deprecated in API level 26. The recommended
        // alternative method is clipOutRect(float, float, float, float),
        // which is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            canvas.clipRect(
                4 * rectInset,
                4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
                Region.Op.DIFFERENCE
            )
        else {
            canvas.clipOutRect(
                4 * rectInset,
                4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset
            )
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawCircularClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowTwo)
        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()
        path.addCircle(
            circleRadius,
            clipRectBottom - circleRadius,
            circleRadius,
            Path.Direction.CCW
        )
        // The method clipPath(path, Region.Op.DIFFERENCE) was deprecated in
        // API level 26. The recommended alternative method is
        // clipOutPath(Path), which is currently available in
        // API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(path)
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawIntersectionClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, rowTwo)
        canvas.clipRect(
            clipRectLeft,
            clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .INTERSECT) was deprecated in API level 26. The recommended
        // alternative method is clipRect(float, float, float, float), which
        // is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,
                clipRectBottom,
                Region.Op.INTERSECT
            )
        } else {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,
                clipRectBottom
            )
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawCombinedClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowThree)
        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + circleRadius + rectInset,
            circleRadius,
            Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset,
            Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, rowThree)
        path.rewind()
        path.addRoundRect(
            rectF,
            clipRectRight / 4,
            clipRectRight / 4,
            Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawOutsideClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowFour)
        canvas.clipRect(
            2 * rectInset,
            2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawTranslatedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.GREEN
        // Align the RIGHT side of the text with the origin.
        paint.textAlign = Paint.Align.LEFT
        // Apply transformation to canvas.
        canvas.translate(columnTwo, textRow)
        // Draw text.
        canvas.drawText(
            context.getString(R.string.translated),
            clipRectLeft,
            clipRectTop,
            paint)
        canvas.restore()
    }

    // Important: When you use View classes provided by the Android system,
    // the system clips views for you to minimize overdraw.
    // When you use custom View classes and override the onDraw() method,
    // clipping what you draw becomes your responsibility.
    private fun drawSkewedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.YELLOW
        paint.textAlign = Paint.Align.RIGHT
        // Position text.
        canvas.translate(columnTwo, textRow)
        // Apply skew transformation.
        canvas.skew(0.2f, 0.3f)
        canvas.drawText(
            context.getString(R.string.skewed),
            clipRectLeft,
            clipRectTop,
            paint)
        canvas.restore()
    }

    private fun drawQuickRejectExample(canvas: Canvas) {
        val inClipRectangle = RectF(clipRectRight / 2,
            clipRectBottom / 2,
            clipRectRight * 2,
            clipRectBottom * 2)

        val notInClipRectangle = RectF(RectF(clipRectRight + 1,
            clipRectBottom + 1,
            clipRectRight * 2,
            clipRectBottom * 2))

        canvas.save()
        canvas.translate(columnOne, rejectRow)
        canvas.clipRect(
            clipRectLeft,
            clipRectTop,
            clipRectRight,
            clipRectBottom
        )
        if (canvas.quickReject(
                inClipRectangle, Canvas.EdgeType.AA)
        ) {
            canvas.drawColor(Color.WHITE)
        } else {
            canvas.drawColor(Color.BLACK)
            canvas.drawRect(inClipRectangle, paint
            )
        }
        canvas.restore()
    }
}