package com.skripsi.area31.enroll.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.databinding.FragmentBottomsheetEnrollBinding
import com.skripsi.area31.enroll.injection.DaggerEnrollComponent
import com.skripsi.area31.enroll.injection.EnrollComponent
import com.skripsi.area31.enroll.presenter.EnrollPresenter
import com.skripsi.area31.utils.Constants
import kotlinx.android.synthetic.main.fragment_bottomsheet_enroll.*
import java.io.IOException
import javax.inject.Inject

class EnrollFragment : BottomSheetDialogFragment(), EnrollContract {
  private var daggerBuild: EnrollComponent = DaggerEnrollComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: EnrollPresenter
  @Inject lateinit var gson: Gson

  private lateinit var binding: FragmentBottomsheetEnrollBinding
  private var barcodeDetector: BarcodeDetector? = null
  private var cameraSource: CameraSource? = null
  private lateinit var intentData: String
  private lateinit var accessToken: String

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_enroll, container,
        false)
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    accessToken = gson.fromJson(
        context?.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken

    presenter.attach(this)
    presenter.subscribe()

    initialiseDetectorsAndSources()
    with(binding) {
      tvManualInput.setOnClickListener {
        stopCamera()
        cameraSource?.release()
        layoutCamera.visibility = View.GONE
        tvManualInput.visibility = View.GONE
        layoutManualJoin.visibility = View.VISIBLE
      }
    }
  }

  private fun initialiseDetectorsAndSources() {
    barcodeDetector = BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build()

    cameraSource = CameraSource.Builder(context, barcodeDetector).setRequestedPreviewSize(1920,
        1080).setAutoFocusEnabled(true).build()

    surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
      override fun surfaceCreated(holder: SurfaceHolder) {
        try {
          cameraSource.let {
            it?.start(surfaceView.holder)
          }
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }

      override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        try {
          cameraSource.let {
            it?.start(surfaceView.holder)
          }
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }

      override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraSource.let {
          it?.stop()
        }
      }
    })

    barcodeDetector.let {
      it?.setProcessor(object : Detector.Processor<Barcode> {
        override fun release() {
          //No implementation required
        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
          val barcode = detections.detectedItems
          if (barcode.size() != 0) {
            binding.surfaceView.post {
              if (barcode.valueAt(0).displayValue.startsWith("QR")) {
                stopCamera()
                showProgress(true)
                intentData = barcode.valueAt(0).displayValue
                val idSlot = intentData.substringAfter("idSlot=").substringBefore(')')
                val fcm = intentData.substringAfter(")")
                //              presenter.createBooking(idSlot, fcm, accessToken)
                Toast.makeText(context, intentData, Toast.LENGTH_LONG).show()
              }
            }
            vibratePhone()
          }
        }
      })
    }
    barcodeDetector
  }

  fun stopCamera() {
    if (null != barcodeDetector) {
      barcodeDetector?.release()
    }
  }

  fun Fragment.vibratePhone() {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
      vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
      vibrator.vibrate(200)
    }
  }

  fun showProgress(show: Boolean) {
    if (show) {
      binding.progressBar.visibility = View.VISIBLE
    } else {
      binding.progressBar.visibility = View.GONE
    }
  }

  override fun onFailed(message: String) {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }


}