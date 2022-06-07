package com.company.dietrichalex.qrwork.qr_code

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.company.dietrichalex.qrwork.R
import com.company.dietrichalex.qrwork.databinding.FragmentGeneratorBinding
import com.company.dietrichalex.qrwork.inter_face.Transition
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix


class GeneratorFragment : Fragment() {
    private var _binding: FragmentGeneratorBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var transition: Transition? = null

    private var bitmap: Bitmap? = null

    private var antiOverloadFlag = false

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
        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextTextPersonName.addTextChangedListener {
            antiOverloadFlag = false
        }

        binding.generateImageBtn.setOnClickListener {
            try {
                if (antiOverloadFlag) return@setOnClickListener

                bitmap = textToImageEncode(binding.editTextTextPersonName.text.toString())
                binding.qrCodeIv.setImageBitmap(bitmap)

                binding.shareImageBtn.visibility = View.VISIBLE

                antiOverloadFlag = true
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    getString(R.string.edit_text_toast), Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.shareImageBtn.setOnClickListener {
            if (bitmap == null) {
                Toast.makeText(
                    context,
                    getString(R.string.no_image_toast), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                val path = MediaStore.Images.Media.insertImage(
                    activity?.contentResolver,
                    bitmap,
                    "Image Description",
                    null
                )
                val uri = Uri.parse(path)

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/jpeg"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(intent, "Share Image"))
            }
        }
    }

    @Throws(WriterException::class)
    private fun textToImageEncode(Value: String?): Bitmap? {
        val bitMatrix: BitMatrix = try {
            MultiFormatWriter().encode(
                Value,
                BarcodeFormat.QR_CODE,
                QR_CODE_WIDTH,
                QR_CODE_WIDTH,
                null
            )
        } catch (IllegalArgumentException: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] =
                    if (bitMatrix[x, y]) resources.getColor(R.color.black)
                    else resources.getColor(R.color.white)
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

    companion object {
        const val QR_CODE_WIDTH = 500

        @JvmStatic
        fun newInstance() = GeneratorFragment()
    }
}