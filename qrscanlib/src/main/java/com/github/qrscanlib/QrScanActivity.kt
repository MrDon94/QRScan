package com.github.qrscanlib

import android.content.pm.ActivityInfo
import android.graphics.Matrix
import android.graphics.Rect
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import de.markusfisch.android.cameraview.widget.CameraView

/**
 * @author: cd
 * @projectName: QRScan
 * @description:
 * @date: 2024/3/19 11:01
 */
class QrScanActivity : AppCompatActivity() {
    private lateinit var cameraView: CameraView
    private lateinit var viewfinderView: ViewfinderView

    private val frameRoi = Rect()
    private val matrix = Matrix()
    private var frameMetrics = FrameMetrics()
    private var ignoreNext: String? = null
    private var decoding = true

    private var requestCameraPermission = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scan)
        cameraView = findViewById(R.id.camera_view)
        viewfinderView = findViewById(R.id.viewfinderView)

        initCameraView()
    }

    override fun onResume() {
        super.onResume()
        System.gc()
        if (hasCameraPermission(requestCameraPermission)) {
            //这里做个延迟避免界面未加载完成启动相机预览失败问题
            cameraView.postDelayed({
                openCamera()
            },150)
        }
        requestCameraPermission = false
    }

    override fun onPause() {
        super.onPause()
        closeCamera()
    }

    private fun closeCamera() {
        cameraView.close()
    }

    private fun openCamera() {
        cameraView.openAsync(
            CameraView.findCameraId(
                @Suppress("DEPRECATION")
                Camera.CameraInfo.CAMERA_FACING_BACK
            )
        )
    }

    private fun initCameraView() {
        cameraView.setUseOrientationListener(true)
        @Suppress("DEPRECATION")
        cameraView.setOnCameraListener(object : CameraView.OnCameraListener {
            override fun onConfigureParameters(
                parameters: Camera.Parameters
            ) {
                val sceneModes = parameters.supportedSceneModes
                sceneModes?.let {
                    for (mode in sceneModes) {
                        if (mode == Camera.Parameters.SCENE_MODE_BARCODE) {
                            parameters.sceneMode = mode
                            break
                        }
                    }
                }
                CameraView.setAutoFocus(parameters)
            }

            override fun onCameraError() {
                Toast.makeText(this@QrScanActivity, "无法访问相机，请重试。", Toast.LENGTH_SHORT).show()
            }

            override fun onCameraReady(camera: Camera) {
                frameMetrics = FrameMetrics(
                    cameraView.frameWidth,
                    cameraView.frameHeight,
                    cameraView.frameOrientation
                )
                updateFrameRoiAndMappingMatrix()
                ignoreNext = null
                decoding = true
                // These settings can't change while the camera is open.
                val options = ZxingCpp.ReaderOptions().apply {
                    tryHarder = false
                    tryRotate = true
                    tryInvert = true
                    tryDownscale = true
                    maxNumberOfSymbols = 1
                }
                var useLocalAverage = false
                camera.setPreviewCallback { frameData, _ ->
                    if (decoding) {
                        useLocalAverage = useLocalAverage xor true
                        ZxingCpp.readByteArray(
                            frameData,
                            frameMetrics.width,
                            frameRoi.left, frameRoi.top,
                            frameRoi.width(), frameRoi.height(),
                            frameMetrics.orientation,
                            options.apply {
                                // By default, ZXing uses LOCAL_AVERAGE, but
                                // this does not work well with inverted
                                // barcodes on low-contrast backgrounds.
                                binarizer = if (useLocalAverage) {
                                    ZxingCpp.Binarizer.LOCAL_AVERAGE
                                } else {
                                    ZxingCpp.Binarizer.GLOBAL_HISTOGRAM
                                }
                                formats = setOf(ZxingCpp.BarcodeFormat.QR_CODE.name).toFormatSet()
                            }
                        )?.let { results ->
                            val result = results.first()
                            if (result.text != ignoreNext) {
                                postResult(result)
                                decoding = false
                            }
                        }
                    }
                }
            }

            override fun onPreviewStarted(camera: Camera) {
            }

            override fun onCameraStopping(camera: Camera) {
                camera.setPreviewCallback(null)
            }
        })
    }

    private fun updateFrameRoiAndMappingMatrix() {
        val viewRect = cameraView.previewRect
        val viewRoi = if (viewfinderView.frame.width() < 1) {
//            Log.i("cdcdcd","使用默认相机宽高")
            viewRect
        } else {
//            Log.i("cdcdcd","使用扫描框宽高")
            viewfinderView.frame
        }
        frameRoi.setFrameRoi(frameMetrics, viewRect, viewRoi)
        matrix.setFrameToView(frameMetrics, viewRect, viewRoi)
    }

    private fun postResult(result: ZxingCpp.Result) {
        cameraView.post {
            scanFeedback()
//            Log.i("cdcdcd","扫描结果：${result.text}")
            val data = intent
            data.putExtra(Intents.QR_SCAN_RESULT,result.text)
            setResult(RESULT_OK,data)
            finish()
        }
    }

    fun Set<String>.toFormatSet(): Set<ZxingCpp.BarcodeFormat> = map {
        ZxingCpp.BarcodeFormat.valueOf(it)
    }.toSet()

    override fun finish() {
        super.finish()
        //解决切换到上个界面后，横竖屏多次切换的问题
        if (Build.VERSION.SDK_INT >= 27) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
}