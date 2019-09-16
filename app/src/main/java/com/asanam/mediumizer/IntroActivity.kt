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
        setButtonCtaClickListener {
            openMediumApp()
        }

        pageScrollDuration = 500
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPageScrollInterpolator(android.R.interpolator.fast_out_slow_in)
        }

        addSlide(
            SimpleSlide.Builder()
                .title("open Medium")
                .description("Open Medium and select the articles which you want to read.")
                .image(R.drawable.splash)
                .background(android.R.color.white)
                .backgroundDark(android.R.color.background_dark)
                .layout(R.layout.intro_slide)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title("Select The Article")
                .description("select the Article which you want to read and click on it.")
                .image(R.drawable.article)
                .background(android.R.color.white)
                .backgroundDark(android.R.color.background_dark)
                .layout(R.layout.intro_slide)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title("Share the article to Mediumizer")
                .description("After selecting the article share it to Mediumizer to read the premium article for free ")
                .image(R.drawable.share)
                .background(android.R.color.white)
                .backgroundDark(android.R.color.background_dark)
                .layout(R.layout.intro_slide)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title("Read full article in Mediumizer")
                .description("After sharing the article, Mediumizer opens the whole article to read without any restrictions. No Need to upgrade your membership in Medium.")
                .image(R.drawable.full_article)
                .background(android.R.color.white)
                .backgroundDark(android.R.color.background_dark)
                .layout(R.layout.intro_slide)
                .canGoForward(false)
                .build()
        )
        autoplay(2500, INFINITE)
    }

    private fun openMediumApp() {
        var intent = applicationContext.packageManager.getLaunchIntentForPackage(MEDIUM_TAG)
        if (intent == null) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$MEDIUM_TAG")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }
}