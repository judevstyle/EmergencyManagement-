package com.zine.ketotime.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.idon.emergencmanagement.R
import com.ssoft.candeliveryrider.view.TypeFactory


class CustomButton : AppCompatButton {

    private var typefaceType = 0
    private var mFontFactory: TypeFactory? = null


    constructor(
        context: Context,
        attrs: AttributeSet
    ):super(context, attrs) {

        applyCustomFont(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyle: Int
    ):super(context, attrs, defStyle) {

        applyCustomFont(context, attrs)
    }

    constructor(context: Context):super(context) {

    }

    private fun applyCustomFont(
        context: Context,
        attrs: AttributeSet
    ) {
        val array = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomTextView,
            0, 0
        )
        typefaceType = try {
            array.getInteger(R.styleable.CustomTextView_font_name, 0)
        } finally {
            array.recycle()
        }
        if (!isInEditMode) {
            setTypeface(getTypeFace(typefaceType))
        }
    }

    fun getTypeFace(type: Int): Typeface? {
        if (mFontFactory == null) mFontFactory = TypeFactory(context!!)
        return when (type) {
            Constants.A_BOLD -> mFontFactory!!.kanitBold
            Constants.A_LIGHT -> mFontFactory!!.kanitLight
            Constants.A_Italic -> mFontFactory!!.kanitItalic
            Constants.A_MEDIUM -> mFontFactory!!.kanitMedium
            else -> mFontFactory!!.kanitRegular
        }
    }

    interface Constants {
        companion object {
            const val A_BOLD = 1
            const val A_LIGHT = 2
            const val A_REGULAR = 3
            const val A_Italic = 4
            const val A_MEDIUM = 5

        }
    }
}