package com.asanam.mediumizer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import android.os.Build
import android.text.Spanned
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.view.View
import android.widget.Toast
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide

private const val MEDIUM_TAG = "com.medium.reader"
class MainIntroActivity : IntroActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isButtonBackVisible = false
        isButtonNextVisible = true
        isButtonCtaVisible = true

        val labelSpan = TypefaceSpan(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) "sans-serif-medium" else "sans serif"
        )
        val label = SpannableString
            .valueOf("Go to Medium")
        label.setSpan(labelSpan, 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        buttonCtaLabel = label
        buttonCtaClickListener = View.OnClickListener {
            Toast.makeText(it.context, "DOne", Toast.LENGTH_SHORT).show()
            openMediumApp()
        }

        pageScrollDuration = 500
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPageScrollInterpolator(android.R.interpolator.fast_out_slow_in)
        }

        addSlide(
            SimpleSlide.Builder()
                .title("open Medium")
                .description("open medium")
                .image(R.drawable.not_found)
                .background(android.R.color.white)
                .backgroundDark(android.R.color.background_dark)
                .layout(R.layout.intro_slide)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title("open Medium")
                .description("open medium")
                .image(R.drawable.not_found)
                .background(android.R.color.white)
                .backgroundDark(android.R.color.background_dark)
                .layout(R.layout.intro_slide)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title("open Medium")
                .description("open medium")
                .image(R.drawable.not_found)
                .background(android.R.color.white)
                .backgroundDark(android.R.color.background_dark)
                .layout(R.layout.intro_slide)
                .canGoForward(false)
                .build()
        )
    }

    private fun openMediumApp() {
        val intent = applicationContext.packageManager.getLaunchIntentForPackage(MEDIUM_TAG)
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("market://details?id=$packageName");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        }
    }
}