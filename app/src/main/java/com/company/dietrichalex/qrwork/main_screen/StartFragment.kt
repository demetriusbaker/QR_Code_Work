package com.company.dietrichalex.qrwork.main_screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.company.dietrichalex.qrwork.R
import com.company.dietrichalex.qrwork.databinding.FragmentStartBinding
import com.company.dietrichalex.qrwork.inter_face.Transition
import com.company.dietrichalex.qrwork.qr_code.ScannerActivity

class StartFragment : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var transition: Transition? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        transition = context as? Transition
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.importQRBtn.setOnClickListener {
            transition?.openImportFragment()
        }

        binding.cameraQRBtn.setOnClickListener {
            startActivity(Intent(context, ScannerActivity::class.java))
        }

        binding.generateBtn.setOnClickListener {
            transition?.openGeneratorFragment()
        }

        binding.githubQrImage.setOnClickListener {
            val uri = Uri.parse(getString(R.string.github_link))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        transition = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = StartFragment()
    }
}