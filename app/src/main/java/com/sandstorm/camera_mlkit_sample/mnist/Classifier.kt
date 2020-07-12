package com.sandstorm.camera_mlkit_sample.mnist

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.Executors
import kotlin.coroutines.suspendCoroutine

class Classifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    var isInitialized = false

    private val executoreService = Executors.newCachedThreadPool()
    private var inputImageWidth: Int = 0
    private var inputImageHeight: Int = 0
    private var modelInputSize: Int = 0

    suspend fun initializeAsync(){
        suspendCoroutine <Void> {
//            executoreService.execute{
//                try {
//                    initializeInterpreter()
//                    Log.d("Model Import :","Successful")
//                    isInitialized = true
//                }catch (e : IOException){
//                    isInitialized = false
//                    Log.d("Model Import :","Failed")
//                }
//            }
            try {
                initializeInterpreter()
                Log.d("Model Import :","Successful")
                isInitialized = true
            }catch (e : IOException) {
                isInitialized = false
                Log.d("Model Import :", "Failed")
            }
        }
    }

    @Throws(IOException::class)
    private fun initializeInterpreter(){
        val assetManager = context.assets
        val model = loadModelFile(assetManager)

        val options = Interpreter.Options()
        options.setUseNNAPI(true)
        val interpreter = Interpreter(model,options)

        val inputShape = interpreter.getInputTensor(0).shape()
        inputImageWidth = inputShape[1]
        inputImageHeight = inputShape[2]
        modelInputSize = FLOAT_TYPE_SIZE * inputImageWidth * inputImageHeight * PIXEL_SIZE

        this.interpreter = interpreter
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager): ByteBuffer {
        val fileDescriptor = assetManager.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength)
    }

    private fun classify(bitmap: Bitmap): String{
        if(!isInitialized){
            throw IllegalStateException("TF Lite is not initialized yet!")
        }

        var startTime: Long
        var endTime: Long
        startTime = System.nanoTime()
        val resizedImage = Bitmap.createScaledBitmap(bitmap,inputImageWidth,inputImageHeight, true)
        val byteBuffer = convertBitmapToByteBuffer(resizedImage)
        endTime = (System.nanoTime()-startTime)/1000000
        Log.d("Preprocessing Time : ",endTime.toString())

        startTime = System.nanoTime()
        val result = Array(1){FloatArray(OUTPUT_CLASS_COUNT)}
        interpreter?.run(byteBuffer,result)
        endTime = (System.nanoTime() - startTime)/1000000
        Log.d("Inference Time : ",endTime.toString())
        return getOutputString(result[0])
    }

    private fun getOutputString(output: FloatArray): String {
        val maxIndex = output.indices.maxBy { output[it] } ?: -1
        return "Prediction %d with confidence %f ".format(maxIndex,output[maxIndex])
    }


    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
            val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(inputImageWidth*inputImageHeight)
        bitmap.getPixels(pixels,0,bitmap.width,0,0,bitmap.width,bitmap.height)

        for(pixelValue in pixels){
            val r = (pixelValue shr 16 and 0xFF)
            val g = (pixelValue shr 8 and 0xFF)
            val b = (pixelValue and 0xFF)

            val normalizedPixelValue = (r + g + b)/3.0f / 255.0f
            byteBuffer.putFloat(normalizedPixelValue)
        }
        return byteBuffer
    }

    suspend fun classifyAsync(bitmap: Bitmap) = suspendCoroutine<String> {
        val result = classify(bitmap)
        it.resumeWith(Result.success(result))
    }

    suspend fun closeAsync() = suspendCoroutine<Void> {
       interpreter?.close()
        Log.d("Interpreter Log : ","Interpreter Closed")
    }
    companion object {
        private val MODEL_FILE = "mnist_with_softmax_2.tflite"
        private val FLOAT_TYPE_SIZE = 4
        private val PIXEL_SIZE = 1
        private val OUTPUT_CLASS_COUNT = 10
    }
}