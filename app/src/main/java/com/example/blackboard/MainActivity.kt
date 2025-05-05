package com.example.blackboard

import android.R
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.blackboard.databinding.ActivityMainBinding

// will add:
// * grid
// * layers
// * pan
// * zoom
// * rotate
// may add:
// * grid snapping
// * lines
// * arrows
// * shapes

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var canvasView: CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        canvasView = binding.canvasView

        binding.apply {
            clearButton.setOnClickListener{
                canvasView.clearCanvas()
            }
            colorButton.setOnClickListener{
                canvasView.changeColor()
            }
            lineWidthButton.setOnClickListener{
                canvasView.changeLineWidth()
            }
            undoButton.setOnClickListener{
                canvasView.undo()
            }
            redoButton.setOnClickListener{
                canvasView.redo()
            }
            eraseButton.setOnClickListener{
                canvasView.erase()
            }
        }
    }
}
