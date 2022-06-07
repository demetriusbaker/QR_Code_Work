package com.company.dietrichalex.qrwork.main_screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.company.dietrichalex.qrwork.R
import com.company.dietrichalex.qrwork.inter_face.Transition
import com.company.dietrichalex.qrwork.qr_code.GeneratorFragment
import com.company.dietrichalex.qrwork.qr_code.ImportFragment


class MainActivity : AppCompatActivity(),
    Transition {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment == null) openStartFragment()
    }

    override fun openStartFragment() {
        openFragment(StartFragment.newInstance())
    }

    override fun openImportFragment() {
        openFragment(ImportFragment.newInstance())
    }

    override fun openGeneratorFragment() {
        openFragment(GeneratorFragment.newInstance())
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}