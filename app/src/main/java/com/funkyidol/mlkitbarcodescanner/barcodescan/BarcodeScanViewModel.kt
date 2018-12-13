package com.funkyidol.mlkitbarcodescanner.barcodescan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.funkyidol.mlkitbarcodescanner.api.BuycottApiService
import com.funkyidol.mlkitbarcodescanner.api.RetrofitModule
import com.funkyidol.mlkitbarcodescanner.model.BarcodeResultPojo
import com.funkyidol.mlkitbarcodescanner.model.QrCode
import com.funkyidol.mlkitbarcodescanner.util.SingleLiveEvent
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import io.fotoapparat.parameter.Resolution
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber

class BarcodeScanViewModel : ViewModel() {
    private val qrList = arrayListOf<QrCode>()

    private lateinit var fbVisionMetadata: FirebaseVisionImageMetadata

    private lateinit var fbVisionImage: FirebaseVisionImage

    val disableScanningEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showLoadingEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    var barcodeInfoLiveData: MutableLiveData<BarcodeResultPojo> = MutableLiveData<BarcodeResultPojo>()

    val retrofit: Retrofit = RetrofitModule().initRetrofit()

    init {
        disableScanningEvent.value = false
        showLoadingEvent.postValue(false)
    }

    val barcodeDetectorOptions =
        FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
            .build()

    fun runBarcodeScanner(byteArray: ByteArray, size: Resolution, imgRotation: Int) {
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

        fbVisionMetadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(size.width)   // 480x360 is typically sufficient for
            .setHeight(size.height)  // image recognition
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12)
            .setRotation(rotation)
            .build()
        fbVisionImage = FirebaseVisionImage.fromByteArray(byteArray, fbVisionMetadata)

        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(barcodeDetectorOptions)
        detector.detectInImage(fbVisionImage).addOnSuccessListener {
            for (firebaseBarcode in it) {
                /*when (firebaseBarcode.valueType) {
                    //Handle the URL here
                    FirebaseVisionBarcode.TYPE_URL ->
                        qrList.add(QrCode("URL", firebaseBarcode.displayValue))
                    // Handle the contact info here, i.e. address, name, phone, etc.
                    FirebaseVisionBarcode.TYPE_CONTACT_INFO ->
                        qrList.add(
                            QrCode(
                                "Contact",
                                firebaseBarcode.contactInfo?.title
                            )
                        )
                    // Handle the wifi here, i.e. firebaseBarcode.wifi.ssid, etc.
                    FirebaseVisionBarcode.TYPE_WIFI ->
                        qrList.add(QrCode("WiFi", firebaseBarcode.wifi?.ssid))
                    // Handle the driver license barcode here, i.e. City, Name, Expiry, etc.
                    FirebaseVisionBarcode.TYPE_DRIVER_LICENSE ->
                        qrList.add(
                            QrCode(
                                "Driver License",
                                firebaseBarcode.driverLicense?.licenseNumber
                            )
                        )
                    //Handle more types
                    else ->
                        qrList.add(
                            QrCode(
                                "Generic",
                                firebaseBarcode.displayValue
                            )
                        )
                    //None of the above type was detected, so extract the value from the barcode
                }*/
                showLoadingEvent.postValue(true)
                disableScanningEvent.value = true
//                Timber.d(firebaseBarcode.toString())
                fetchBarCodeInfo(firebaseBarcode.displayValue?.trim())

            }
        }.addOnFailureListener {
            // Task failed with an exception
//            Toast.makeText(baseContext, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
        }


    }

    private fun fetchBarCodeInfo(barcode: String?) {

        Timber.d(barcode)
        retrofit.create(BuycottApiService::class.java).lookupBarcode(barcode)
            .enqueue(object : Callback<BarcodeResultPojo> {
                override fun onFailure(call: Call<BarcodeResultPojo>?, t: Throwable?) {
                    showLoadingEvent.postValue(false)
                }

                override fun onResponse(call: Call<BarcodeResultPojo>?, response: Response<BarcodeResultPojo>?) {
                    Timber.d(response?.body().toString())
                    displayResult(response?.body())

                    showLoadingEvent.postValue(false)
                }
            })

    }

    private fun displayResult(barcodeResultPojo: BarcodeResultPojo?) {
        barcodeInfoLiveData.value = barcodeResultPojo
    }

}