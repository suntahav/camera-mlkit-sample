package com.sandstorm.camera_mlkit_sample.utils

import android.graphics.Color
import android.graphics.Path

data class TracePath(
    var color: Color,
    var emboss: Boolean,
    var blur: Boolean,
    var strokeWidth: Int,
    var path: Path
)