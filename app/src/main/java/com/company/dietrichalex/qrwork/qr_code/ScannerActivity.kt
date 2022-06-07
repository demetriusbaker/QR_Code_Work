package com.company.dietrichalex.qrwork.qr_code

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.company.dietrichalex.qrwork.R
import com.google.android.material.snackbar.Snackbar
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView


class ScannerActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {
    private lateinit var zbView: ZBarScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zbView = ZBarScannerView(this)
        setContentView(zbView)
    }

    override fun onPause() {
        super.onPause()
        zbView.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        zbView.setResultHandler(this)
        zbView.startCamera()
    }

    @SuppressLint("ShowToast")
    override fun handleResult(p0: Result?) {
        val link = p0?.contents.toString()
        val view = this.findViewById<View>(android.R.id.content)
        val snackBar = Snackbar.make(view, link, Snackbar.LENGTH_LONG)
        snackBar.setAction("CLICK") {
            try {
                if (!link.contains("http"))
                    throw Exception()

                val uri = Uri.parse(link)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: Exception) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val label = getString(R.string.copied_toast)
                val clip = ClipData.newPlainText(label, link)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
            }
        }
        snackBar.show()
    }
}