package com.example.hp33cfr


import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.text.take


fun mantisse() {
    val s = abs(calcul.x).toString()
    message = s.take(11)
    flashMessage(4000)
}


fun affSmart(): String {
    // expérimental affichage y
    infomoi = calcul.y.toString()

    calcul.x = calcul.x.roundTo12Digits()

    val s = "%${11}e".format(Locale.US, calcul.x) // Formate large
    //  Log.d("exp", "  $s")
    val parts = s.split("e") // Sépare le nombre de l'exposant
    val mantisse = parts[0] // La mantisse
    val grandeur = abs(parts[1].toInt())
    val exposant = "E" + parts[1].take(4) // Reconstruit l'exposant
//Log.d("exptt", "  $mantisse --   ${parts[1]}")
    // On garde la mantisse, mais on la coupe pour que (mantisse + exposant) = 11
    val tailleMaxMantisse = 11 - exposant.length
    val mantisseCoupee = mantisse.take(tailleMaxMantisse)
    // Log.d("exp", "  $grandeur  --   $parts[1]")
    xEEX = mantisseCoupee + exposant

    var predisplay = ""
    if (fixSciEng == 0) {
        predisplay =    String.format(Locale.US, "%.${fix}f", calcul.x)}
                         //   mantisseCoupee.take(fix) + exposant   }
if (fixSciEng == 1) {

        predisplay = if (grandeur <= 6) {
            calcul.x.toString()
        } else {
            xEEX
        }
    }
    if (fixSciEng == 2) {
        predisplay = formatEngineer(calcul.x)
    }

    return predisplay
}


fun formatEngineer(value: Double): String {
    if (value == 0.0) {
        // Gestion du zéro : l'exposant est 00
        return String.format(Locale.US, "%.${fix}E", 0.0).replace("E+00", "E00")
    }

    val absValue = value.absoluteValue

    // 1. Calcul de l'exposant scientifique de base (puissance de 10)
    val exponent = floor(log10(absValue)).toInt()

    // 2. Ajustement pour obtenir un multiple de 3 inférieur ou égal
    val shift = if (exponent >= 0) exponent % 3 else (3 + (exponent % 3)) % 3
    val engExponent = exponent - shift

    // 3. Ajustement de la mantisse (le nombre devant l'exposant)
    val engMantissa = value / 10.0.pow(engExponent)

    // 4. Construction de la chaîne au format final
    // On force le signe de l'exposant pour coller au style HP (+ ou -)
    val signStr = if (engExponent >= 0) "+" else "-"
    val absExponent = engExponent.absoluteValue

    // Formate l'exposant sur 2 chiffres minimum (ex: 03, 06)
    val formattedExponent = String.format(Locale.US, "%02d", absExponent)

    var formattedMantissa = String.format(Locale.US,  "%.${fix}f", engMantissa)
    formattedMantissa = formattedMantissa.take(7)
    return "$formattedMantissa E$signStr$formattedExponent"
}

fun Double.roundTo12Digits(): Double {
    // 1. Si le nombre est extrêmement petit (ex: inférieur à 1e-13 en valeur absolue), on retourne 0
    if (abs(this) < 1e-13) {
        return 0.0
    }
    // 2. Arrondi à 12 chiffres significatifs (gère aussi les nombres négatifs)
    val mc = MathContext(12, RoundingMode.HALF_UP)
    return BigDecimal(this).round(mc).toDouble()
}
