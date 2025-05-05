package com.example.blackboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


@SuppressLint("ClickableViewAccessibility")
class CanvasView(context:Context, attributes: AttributeSet) : View(context, attributes) {
    private var currentColorCounter = 0
    private var currentColor = Color.WHITE

    // contains data that should be drawn
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private val path = Path()

    // contains the coordinates list to which the paths are based off of
    private val coordList = ArrayList<Pair<Float, Float>>()

    private var currentLineWidth = 1;
    private var paintConfig = Paint().apply {
        isAntiAlias = true
        strokeWidth = 3f * currentLineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
    }

//    private val connection = ClientSocket()
    private val connection = ServerSocket()

    init {
        setOnTouchListener { _, event ->
            val x = event.x
            val y = event.y

            when(event.action) {
                MotionEvent.ACTION_DOWN->startDrawing(x, y)
                MotionEvent.ACTION_MOVE->continueDrawing(x, y)
                MotionEvent.ACTION_UP->endDrawing()
            }
            invalidate()
            true
        }

        /* @todo: handle cases in which the server was not previously started */
        connection.init()
    }

    private fun startDrawing(x: Float, y: Float) {
        path.reset()

        path.moveTo(x, y)
        coordList.add(Pair(x, y))

        // send data
        connection.writeCoordinates(x.toInt(), y.toInt())
    }

    private fun continueDrawing(x: Float, y: Float) {
        // have to error check
        val previousX = coordList.last().first;
        val previousY = coordList.last().second;
        path.quadTo(previousX, previousY, (x + previousX) / 2, (y + previousY) / 2)

        // send data
        connection.writeCoordinates(x.toInt(), y.toInt())

        path.lineTo(previousX, previousY)
        coordList.add(Pair(x, y))

        val paint = Paint(paintConfig)
        paint.color = currentColor
        paths.add(Pair(Path(path), paint))
    }

    private fun endDrawing() {
        connection.writeCoordinates(0, 0)

        // have to error check
        val previousX = coordList.last().first;
        val previousY = coordList.last().second;

        path.lineTo(previousX, previousY)
        coordList.add(Pair(x, y))

        val paint = Paint(paintConfig)
        paint.color = currentColor
        paths.add(Pair(Path(path), paint))
        path.reset()
    }

    fun panCanvas() {
        val xOffset = 10
        val yOffset = 10
    }

    fun clearCanvas() {
        paths.clear()
        invalidate()

        connection.writeClearCanvas()
    }

    fun changeColor() {
        currentColorCounter++

        /* @todo: change this to actually handle hex values */
        if(5 == currentColorCounter) {
            currentColorCounter = 0
        }

        if(0 == currentColorCounter) {
            currentColor = Color.WHITE
        }

        if(1 == currentColorCounter) {
            currentColor = Color.BLUE
        }

        if(2 == currentColorCounter) {
            currentColor = Color.YELLOW
        }

        if(3 == currentColorCounter) {
            currentColor = Color.GREEN
        }

        if(4 == currentColorCounter) {
            currentColor = Color.RED
        }

        connection.writeChangeColor()
    }

    fun changeLineWidth() {
        currentLineWidth++
        if(8 == currentLineWidth) {
            currentLineWidth = 1
        }

        paintConfig.strokeWidth = 3f * currentLineWidth
        connection.writeChangeLineWidth()
    }

    fun undo() {
        connection.writeUndoLastAction()
    }

    fun redo() {
        connection.writeRedoLastAction()
    }

    fun erase() {
        connection.writeEraseSection()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((p, paint) in paths) {
            canvas?.drawPath(p, paint)
        }

        canvas?.drawPath(path, paintConfig)
    }
}
