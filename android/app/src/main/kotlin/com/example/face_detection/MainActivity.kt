package com.example.face_detection

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.annotation.NonNull
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.File

class MainActivity: FlutterActivity(){
    private  val CHANNEL = "com.example.faceDetectionAndroid/faceDetectionAndroid"
    fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "getFaceCountFromImage") {
                val imagePath = call.arguments as String
                val faceCount = detectFaces(imagePath)
                result.success(faceCount)
            } else {
                result.notImplemented()
            }
        }

}
    private fun detectFaces(imagePath: String): Int {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()

        val detector = FaceDetection.getClient(options)
        val imageFile = File(imagePath)
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        var faceCount = 0
        val task = detector.process(inputImage)
        task.addOnSuccessListener { faces ->
            faceCount = faces.size
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
        // Wait for task to finish (synchronous processing, not recommended for production)
        while (!task.isComplete) { /* Wait */ }

        return faceCount
    }


}
