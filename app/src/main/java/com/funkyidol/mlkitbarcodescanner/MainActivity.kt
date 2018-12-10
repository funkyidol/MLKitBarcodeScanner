package com.funkyidol.mlkitbarcodescanner

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.selector.lowestResolution
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fotoapparat: Fotoapparat

    private val permissionsDelegate = PermissionsHelper(this)

    private var permissionsGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionsGranted = permissionsDelegate.hasCameraPermission()

        if (permissionsGranted) {
            cameraView.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestCameraPermission()
        }

        val cameraConfig = CameraConfiguration(
            previewResolution = lowestResolution(),
            frameProcessor = { frame -> runBarcodeScanner(frame.image, frame.size, frame.rotation) })

        fotoapparat =
                Fotoapparat(context = this, view = cameraView, logger = logcat(), cameraConfiguration = cameraConfig)
    }

    override fun onStart() {
        super.onStart()
        if (permissionsGranted) {
            fotoapparat.start()
//            adjustViewsVisibility()
        }
    }

    override fun onStop() {
        super.onStop()
        if (permissionsGranted) {
            fotoapparat.stop()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            permissionsGranted = true
            fotoapparat.start()
//            adjustViewsVisibility()
            cameraView.visibility = View.VISIBLE
        }
    }

    private val qrList = arrayListOf<QrCode>()

    private fun runBarcodeScanner(
        byteArray: ByteArray,
        size: Resolution,
        imgRotation: Int
    ) {
        var rotation: Int = when (imgRotation) {
            0 ->
                FirebaseVisionImageMetadata.ROTATION_0
            90 ->
                FirebaseVisionImageMetadata.ROTATION_90
            180 ->
                FirebaseVisionImageMetadata.ROTATION_180
            270 ->
                FirebaseVisionImageMetadata.ROTATION_270
            else ->
                FirebaseVisionImageMetadata.ROTATION_0
        }

        val metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(size.width)   // 480x360 is typically sufficient for
            .setHeight(size.height)  // image recognition
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12)
            .setRotation(rotation)
            .build()
        val image = FirebaseVisionImage.fromByteArray(byteArray, metadata)

        //Optional : Define what kind of barcodes you want to scan
        val options =
            FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                .build()

        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        detector.detectInImage(image).addOnSuccessListener {
            qrList.clear()
//            adapter.notifyDataSetChanged()
            for (firebaseBarcode in it) {
                when (firebaseBarcode.valueType) {
                    //Handle the URL here
                    FirebaseVisionBarcode.TYPE_URL ->
                        qrList.add(QrCode("URL", firebaseBarcode.displayValue))
                    // Handle the contact info here, i.e. address, name, phone, etc.
                    FirebaseVisionBarcode.TYPE_CONTACT_INFO ->
                        qrList.add(QrCode("Contact", firebaseBarcode.contactInfo?.title))
                    // Handle the wifi here, i.e. firebaseBarcode.wifi.ssid, etc.
                    FirebaseVisionBarcode.TYPE_WIFI ->
                        qrList.add(QrCode("WiFi", firebaseBarcode.wifi?.ssid))
                    // Handle the driver license barcode here, i.e. City, Name, Expiry, etc.
                    FirebaseVisionBarcode.TYPE_DRIVER_LICENSE ->
                        qrList.add(QrCode("Driver License", firebaseBarcode.driverLicense?.licenseNumber))
                    //Handle more types
                    else ->
                        qrList.add(QrCode("Generic", firebaseBarcode.displayValue))
                    //None of the above type was detected, so extract the value from the barcode
                }
                fotoapparat.stop()
                Log.d("BarcodeScan", qrList.toString())
            }
        }.addOnFailureListener {
            // Task failed with an exception
            Toast.makeText(baseContext, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
        }


    }
}
