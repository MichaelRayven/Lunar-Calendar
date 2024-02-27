package com.michaelrayven.lunarcalendar.types

import com.michaelrayven.lunarcalendar.R

enum class Sign(
    val charCode: String,
    val iconResId: Int
) {
    ARIES("a", R.drawable.ic_aries),
    TAURUS("s", R.drawable.ic_taurus),
    GEMINI("d", R.drawable.ic_gemini),
    CANCER("f", R.drawable.ic_cancer),
    LEO("g", R.drawable.ic_leo),
    VIRGO("h", R.drawable.ic_virgo),
    LIBRA("j", R.drawable.ic_libra),
    SCORPIUS("k", R.drawable.ic_scorpion),
    SAGITTARIUS("l", R.drawable.ic_sagittarius),
    CAPRICORNUS("z", R.drawable.ic_capricorn),
    AQUARIUS("x", R.drawable.ic_aquarius),
    PISCES("c", R.drawable.ic_pisces);

    var extra: String = ""

    companion object {
        fun getByCharCode(charCode: String): Sign? {
            return entries.toTypedArray().find {
                it.charCode == charCode
            }
        }
    }
}