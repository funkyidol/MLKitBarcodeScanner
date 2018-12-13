package com.funkyidol.mlkitbarcodescanner.barcodescan

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.funkyidol.mlkitbarcodescanner.R
import com.funkyidol.mlkitbarcodescanner.model.BarcodeResultPojo
import com.funkyidol.mlkitbarcodescanner.util.PermissionsHelper
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.selector.lowestResolution
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class BarcodeScanActivity : AppCompatActivity() {

    private lateinit var fotoapparat: Fotoapparat

    private val permissionsHelper = PermissionsHelper(this)

    private var permissionsGranted: Boolean = false

    private lateinit var barcodeViewModel: BarcodeScanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        barcodeViewModel = ViewModelProviders.of(this).get(BarcodeScanViewModel::class.java)

        permissionsGranted = permissionsHelper.hasCameraPermission()

        if (permissionsGranted) {
            cameraView.visibility = View.VISIBLE
        } else {
            permissionsHelper.requestCameraPermission()
        }

        val cameraConfig = CameraConfiguration(
            previewResolution = lowestResolution(),
            frameProcessor = { frame -> runBarcodeScanner(frame.image, frame.size, frame.rotation) })

        fotoapparat =
                Fotoapparat(context = this, view = cameraView, logger = logcat(), cameraConfiguration = cameraConfig)

        barcodeViewModel.disableScanningEvent.observe(this, Observer { disableScanning ->
            if (disableScanning) {
                fotoapparat.stop()
            } else {
                fotoapparat.start()
                iv_refresh.visibility = View.GONE
            }
        })

        barcodeViewModel.showLoadingEvent.observe(this, Observer { showLoading ->
            Timber.d(showLoading.toString())
            if (showLoading) {
                pb_loading.visibility = View.VISIBLE
            } else {
                pb_loading.visibility = View.GONE
            }
        })

        barcodeViewModel.barcodeInfoLiveData.observe(this, Observer { barcodePojo ->
            if (barcodePojo != null && barcodePojo.productCount!!.compareTo(0) > 0) {
                iv_refresh.visibility = View.VISIBLE
                ll_content.visibility = View.VISIBLE
                tv_barcode.text = barcodePojo.products?.get(0)?.productName
                tv_info.setOnClickListener { onClickMoreInfo(barcodePojo) }
            } else {
                Toast.makeText(this, "Invalid Barcode", Toast.LENGTH_LONG).show()
            }
        })

        iv_refresh.setOnClickListener {
            fotoapparat.start()
            ll_content.visibility = View.GONE
            iv_refresh.visibility = View.GONE
        }
    }

    private fun onClickMoreInfo(barcodePojo: BarcodeResultPojo) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, barcodePojo.products?.get(0)?.productName) // query contains search string
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        if (permissionsGranted) {
            fotoapparat.start()
            ll_content.visibility = View.GONE
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
        if (permissionsHelper.resultGranted(requestCode, permissions, grantResults)) {
            permissionsGranted = true
            fotoapparat.start()
//            adjustViewsVisibility()
            cameraView.visibility = View.VISIBLE
        }
    }

    private fun runBarcodeScanner(
        byteArray: ByteArray,
        size: Resolution,
        imgRotation: Int) {
        barcodeViewModel.runBarcodeScanner(byteArray, size, imgRotation)
    }
}
