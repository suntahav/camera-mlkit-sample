package com.sandstorm.camera_mlkit_sample.utils

import android.graphics.Path

data class TracePath(
    var color: Int,
    var blur: Boolean,
    var strokeWidth: Float,
    var path: Path
)