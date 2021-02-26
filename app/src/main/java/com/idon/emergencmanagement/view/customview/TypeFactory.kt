package com.ssoft.candeliveryrider.view

import android.content.Context
import android.graphics.Typeface

class TypeFactory {
    private val A_BOLD = "Kanit-Bold.ttf"
    private val A_LIGHT = "Kanit-Light.ttf"
    private val A_REGULAR = "Kanit-Regular.ttf"
    private val A_MEDIUM = "Kanit-Medium.ttf"
    private val A_Italic = "Kanit-Italic.ttf"


    var kanitBold: Typeface? = null
    var kanitLight: Typeface? = null
    var kanitRegular: Typeface? = null
    var kanitMedium: Typeface? = null
    var kanitItalic: Typeface? = null

    constructor(context: Context) {
        kanitBold = Typeface.createFromAsset(context.assets, A_BOLD)
        kanitLight = Typeface.createFromAsset(context.assets, A_LIGHT)
        kanitRegular = Typeface.createFromAsset(context.assets, A_REGULAR)
        kanitMedium = Typeface.createFromAsset(context.assets, A_MEDIUM)
        kanitItalic = Typeface.createFromAsset(context.assets, A_Italic)

    }

}