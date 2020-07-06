package com.sandstorm.camera_mlkit_sample.scratchpad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sandstorm.camera_mlkit_sample.R

class ScratchPadFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scratch_pad_fragment_layout,container,false)
    }
}