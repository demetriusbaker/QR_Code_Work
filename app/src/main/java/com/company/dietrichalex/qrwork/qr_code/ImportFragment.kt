package com.company.dietrichalex.qrwork.qr_code

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.company.dietrichalex.qrwork.R
import com.company.dietrichalex.qrwork.databinding.FragmentImportBinding
import com.company.dietrichalex.qrwork.inter_face.Transition
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.io.InputStream


class ImportFragment : Fragment() {
    private var _binding: FragmentImportBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var transition: Transition? = null

    private var mayCopy = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        transition = context as? Transition
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            transition?.openStartFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, REQUEST_CODE)
        }

        binding.qrCodeTv.setOnClickListener {
            doActionWithLink()
        }

        binding.qrCodeImageView.setOnClickListener {
            doActionWithLink()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            try {
                if (uri == null) return

                val inputStream: InputStream? = context?.contentResolver?.openInputStream(uri)
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream) ?: return

                val width = bitmap.width
                val height = bitmap.height
                val pixels = IntArray(width * height)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                bitmap.recycle()

                val source = RGBLuminanceSource(width, height, pixels)
                val bBitmap = BinaryBitmap(HybridBinarizer(source))
                val reader = MultiFormatReader()
                try {
                    val result = reader.decode(bBitmap)
                    binding.qrCodeTv.text = result.text

                    mayCopy = true
                } catch (e: Exception) {
                    mayCopy = false
                }
            } catch (e: Exception) {
                mayCopy = false
            }
            binding.qrCodeImageView.setImageURI(uri)
        } else binding.qrCodeTv.text = ""

        if (!mayCopy)
            Toast.makeText(
                context,
                getString(R.string.bad_image_toast), Toast.LENGTH_SHORT
            ).show()
    }

    private fun doActionWithLink() {
        if (!mayCopy) return

        val link = binding.qrCodeTv.text.toString()
        try {
            if (!link.contains("http"))
                throw Exception()

            val uri = Uri.parse(link)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            val board = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val label = "Copied link!"
            val clip = ClipData.newPlainText(label, link)
            board.setPrimaryClip(clip)

            Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE = 43

        @JvmStatic
        fun newInstance() = ImportFragment()
    }
}