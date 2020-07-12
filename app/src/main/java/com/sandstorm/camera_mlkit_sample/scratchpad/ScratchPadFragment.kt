package com.sandstorm.camera_mlkit_sample.scratchpad

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sandstorm.camera_mlkit_sample.R
import com.sandstorm.camera_mlkit_sample.mnist.Classifier
import kotlinx.android.synthetic.main.scratch_pad_fragment_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ScratchPadFragment : Fragment() {
    private lateinit var classifier: Classifier
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scratch_pad_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setListeners()
    }

    private fun initialize() {
        classifier = Classifier(requireContext())
        clear()
        GlobalScope.launch {
            classifier.initializeAsync()
        }
    }

    private fun clear() {
        paintView.clear()
        resultTv.text = ""
    }

    private fun setListeners() {
        classifyBtn.setOnClickListener {
            classifyImage()
        }

        clearBtn.setOnClickListener {
            clear()
        }
    }

    private fun classifyImage() {
        val bitmap = paintView?.mBitmap
        if (bitmap != null && classifier.isInitialized) {
            GlobalScope.launch {
                try {
                    val result = classifier.classifyAsync(bitmap)
                    resultTv.text = result
                } catch (e: Exception) {
                    Log.d("Exception", e.toString())
                }

            }
        }
    }

    override fun onDestroy() {
        runBlocking {
            classifier.closeAsync()
        }
        super.onDestroy()
    }
}