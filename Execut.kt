package com.example.hp33cfr


import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.truncate

class Execut {


    private var sgn = 1
    private var prEex = 0
    private var prtoD = 0.0
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    var t: Double = 0.0
    var lastX: Double = 0.0
    private var textIn = false
    private var first = false
    private var xTemp = 0.0
    private var provi = 0.0
    private var modEnterClx = false
    var fonction = 0
    private var touchee = 0
    var runOnOff = false
    var rtn = 0
    var stackRtn = mutableListOf<Int>(0, 0, 0, 0)
    var registres: MutableList<Double> = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    var degRadGrad = 1 // 2 -> rad , 1 -> deg , 3 -> grad
    private var previous = 0
    private var info2nd = false

    fun execut(touchei: Int) {


        textIn = false
        saisie = ""

        if (tempString != "") {
            xTemp = tempString.toDouble()
            tempString = ""
            textIn = true
        }


        //     gestion ENTER & CLX selon notice
// --------------------------------------------------------------------------------------
         if(touchei != 31 && touchei != 34){
            first = false
            modEnterClx = false
        }


        if (first && touchei == 31 && textIn) {
            modEnterClx = true
            first = false
        }

        if ((touchei == 31 || touchei == 34) && !textIn && fonction == 0) {
            first = true
        }
        
        if (touchei == 31) {
            if (textIn) {

                if (!modEnterClx) {
                    t = z
                    z = y
                    y = x
                    x = xTemp
                    t = z
                    z = y
                    y = x
                    return
                } else {
                    x = xTemp
                    modEnterClx = false
                    first = false
                    return
                }
            }
        }
        // --------------------------------------------------------------------------------------
 // la saisie devient x sans ENTER
        if (textIn) {
            t = z
            z = y
            y = x
            x = xTemp
        }


        // GESTION lastX
        if (touchei == 73 && fonction == 1) {
            t = z
            z = y
            y = x
            x = lastX
            fonction = 0
            return
        }

// ACTION DES TOUCHES

// changement pour touches de fonction

        when (fonction) {
            1 -> {
                decodF(touchei)
            }

            2 -> {
                decodG(touchei)
            }

            0 -> {
                decodDirect(touchei)
            }

            3 -> {
                decodSTO(touchei)
            }

            4 -> {
                decodRCL(touchei)
            }

            5 -> { // STO + STO - STO * STO /
                decodSTOWitch(touchei)
            }

            6 -> { // GO TO
                decodGTO(touchei)
            }

            8 -> { // EEX
                decodEEX(touchei)
            }

            9 -> {
                decodFix(touchei)
            }
        }

        return
    }

    private fun decodFix(touchei: Int) {
        if (touchei in 0..9) {
            fix = touchei
            fonction = 0
            return

        } else {
            fonction = 0
        }
    }


    private fun decodEEX(touchei: Int) {
        // info2nd = false
        // prEex = 0
        message = "E+  "
        flashMessage(300)
        if (x == 0.0) {
            x = 1.0
        }
        if (touchei == 41) {
            message = "E-  "
            flashMessage(300)
            sgn = -1
            return
        }
        if (touchei == 51) {
            return
        }

        if (touchei in 0..9) {
            if (!info2nd) {
                prEex = touchei * 10
                info2nd = true
                message += "$prEex"
                flashMessage(300)
                return
            }
            prEex += touchei
            prEex *= sgn
            prtoD = prEex.toDouble()
            prtoD = 10.0.pow(prtoD)  // y.pow(x)
            fonction = 0
            info2nd = false
            sgn = 1
            x *= prtoD

            display = affSmart()

        }
        return
    }


    fun decodSTOWitch(touchei: Int) {
        if (touchei in 0..7) {
            when (touchee) {

                41 -> {
                    registres[touchei] = registres[touchei] - x
                }

                51 -> {
                    registres[touchei] = registres[touchei] + x
                }

                61 -> {
                    registres[touchei] = registres[touchei] * x
                }

                71 -> {
                    if (x == 0.0) {
                        message = "division by 0"
                        flashMessage()

                    } else {
                        registres[touchei] = registres[touchei] / x
                    }
                }
            }
            fonction = 0
            return

        } else {
            message = "only 0 to 7"
            flashMessage()
        }
        fonction = 0
        return
    }


    private fun decodSTO(touchei: Int) {
        if (touchei == 31) {
            message = "8.8.8.8.8.8.8.8"
            flashMessage(600L)
            fonction = 0
            return
        }

        if (touchei == 41 || touchei == 51 || touchei == 61 || touchei == 71) {
            fonction = 5
            touchee = touchei
            return

        }
        if (touchei in 0..7) {

            registres[touchei] = x
        } else {
            message = "only 0 to 7"
            flashMessage()
        }

        fonction = 0

    }

    private fun decodRCL(touchei: Int) {
        if (touchei in 0..7) {
            t = z
            z = y
            y = x
            x = registres[touchei]
        } else {
            message = "only 0 to 7"
            flashMessage()
        }

        fonction = 0

    }

    private fun decodGTO(touchei: Int) {
        if (!info2nd) {
            if (touchei in (0..9)) {
                previous = touchei * 10
                info2nd = true
                return
            }
        }
        if (info2nd) {
            if (touchei in (0..9)) {
                previous += touchei
                if (previous < 52) {
                    index = previous
                    fonction = 0
                    info2nd = false

                    return
                } else {
                    message = "out of bounds"
                    flashMessage(1000L)
                    info2nd = false
                    fonction = 0
                    return
                }
            }
        }
    }

    private fun decodF(touchei: Int) {

        when (touchei) {

            34 -> { // clAll()
                x = 0.0
                y = 0.0
                z = 0.0
                t = 0.0
            }

            0 -> { //  sqrt()
                if (x >= 0.0) {
                    lastX = x
                    x = sqrt(x)
                } else {
                    fonction = 0
                    message = "x < 0"
                    flashMessage(400)
                }

            }

            4 -> { // ->R
                lastX = x
                x = y * cos(x)
                y = y * sin(lastX)
            }


            5 -> {
                lastX = x
                x = Math.toRadians(x)
            }

            6 -> {
                decimalToHMS()
            }


            3 -> { //power
                lastX = x
                x = y.pow(x)
                y = z
                z = t
            }

            11 -> {
                fixSciEng = 0
                fonction = 9
                return
            }

            12 -> { // SCI
                fixSciEng = 1
                fix = 12
            }

            13 -> { // ENG
                fixSciEng = 2
            }

            21 -> {
                message = "NEVER"
                flashMessage()
            }

            22 -> {
                message = "NEVER"
                flashMessage()
            }

            23 -> {
                message = "NEVER"
                flashMessage()
            }

            24 -> {
                message = "NEVER"
                flashMessage()
            }

            25 -> {
                message = "NEVER"
                flashMessage()
            }

            31 -> {
                message = "OK"
                flashMessage(400)
                fonction = 0
            }

            32 -> {
                message = "for Prgm"
                flashMessage()
            }

            33 -> { //clear reg
                registres.fill(0.0)
            }

            41 -> { // x <= y
                if (!(x <= y)) {
                    index += 1
                }

            }

            51 -> {// x > y
                if (!(x > y)) {
                    index += 1
                }

            }

            61 -> { // x != y
                if (x == y) {
                    index += 1
                }

            }

            71 -> { // x == y
                if (x != y) {
                    index += 1
                }

            }

            7 -> {
                lastX = x
                x = when (degRadGrad) {
                    2 -> sin(x)
                    1 -> sin(x * PI / 180)
                    else -> sin(x * PI / 200)

                }
            }

            8 -> {
                lastX = x
                x = when (degRadGrad) {
                    2 -> cos(x)
                    1 -> cos(x * PI / 180)
                    else -> cos(x * PI / 200)
                }
            }

            9 -> {
                lastX = x
                x = when (degRadGrad) {
                    2 -> tan(x)
                    1 -> tan(x * PI / 180)
                    else -> tan(x * PI / 200)
                }
            }


            1 -> {
                if (x > 0.0) {
                    lastX = x
                    x = ln(x)
                } else {
                    fonction = 0
                    message = "x <= 0"
                    flashMessage(400)
                }

            }

            2 -> {
                if (x > 0.0) {
                    lastX = x
                    x = log10(x)
                } else {
                    fonction = 0
                    message = "x <= 0"
                    flashMessage(400)
                }

            }


            74 -> {
                run.timeexc = 2000L
            }

        }
        fonction = 0
    }

    private fun decodG(touchei: Int) {

        when (touchei) {
            11 -> {
                if (index >= 2) {
                    index -= 1
                }
                message = "STEP $index"
                flashMessage(500)
            }

            12 -> {
                if (runOnOff) {
                    if (rtn > 0) {
                        index = stackRtn[rtn]
                        stackRtn[rtn] = 0
                        rtn -= 1
                    }
                } else index = 1

            }

            13 -> {
                // NOP
            }

            21 -> { // deg
                degRadGrad = 1
            }

            22 -> { //rad
                degRadGrad = 2
            }

            23 -> { //grad
                degRadGrad = 3
            }

            24 -> {
                message = "NEVER"
                flashMessage()
            }

            25 -> {
                message = "NEVER"
                flashMessage()
            }

            34 -> {
                lastX = x
                x = abs(x)
            }


            31 -> {
                mantisse()
                fonction = 0
            }

            32 -> {
                lastX = x
                x = truncate(x)
            }

            33 -> {
                lastX = x
                x = x - truncate(x)
            }

            41 -> { // x < 0
                if (!(x < 0.0)) {
                    index += 1
                }
            }

            7 -> { // asin()
                if (x >= -1 && x <= 1) {
                    lastX = x
                    x = when (degRadGrad) {
                        1 -> Math.toDegrees(asin(x))
                        2 -> asin(x)
                        else -> asin(x * 200 / PI)
                    }
                } else {
                    message = "abs(x) > 1"
                    flashMessage()
                }
            }

            8 -> { // acos()
                if (x >= -1 && x <= 1) {
                    lastX = x
                    x = when (degRadGrad) {
                        1 -> Math.toDegrees(acos(x))
                        2 -> acos(x)
                        else -> acos(x * 200 / PI)
                    }
                } else {
                    message = "abs(x) > 1"
                    flashMessage()
                }
            }

            9 -> {
                lastX = x
                x = when (degRadGrad) {
                    1 -> Math.toDegrees(atan(x))
                    2 -> atan(x)
                    else -> atan(x * 200 / PI)
                }
            }

            51 -> { // x > 0
                if (!(x > 0.0)) {
                    index += 1
                }

            }

            5 -> {
                lastX = x
                x = Math.toDegrees(x)
            }

            6 -> {
                hmsToDecimal()
            }

            0 -> {
                lastX = x
                x = x.pow(2)
            }


            1 -> {
                if (x in 0.0..709.0) {
                    lastX = x
                    x = exp(x)
                } else {
                    fonction = 0
                    message = if (x >= 709.0) "out of bounds" else "x < 0"
                    flashMessage(400)
                }

            }

            2 -> {
                if (x in 0.0..308.0) {
                    lastX = x
                    x = 10.0.pow(x)
                } else {
                    fonction = 0
                    message = if (x >= 308.0) "out of bounds" else "x < 0"
                    flashMessage(400)
                }

            }

            3 -> { // 1/x
                if (x != 0.0) {
                    lastX = x
                    x = 1 / x
                } else {
                    fonction = 0
                    message = "x == 0"
                    flashMessage(400)
                }
            }

            4 -> {
                lastX = x
                // 1. Calcul de la norme
                val r = sqrt(x * x + y * y)
                // 2. Calcul de l'angle en radians
                val radians = atan2(y, x)
                x = r
                if (degRadGrad == 1) {
                    y = Math.toDegrees(radians)
                }
                if (degRadGrad == 2) {
                    y = radians
                }
                if (degRadGrad == 3) {
                    y = radians * 200 / PI
                }

            }

            61 -> { // x != 0.0
                if (x == 0.0) {
                    index += 1
                }
            }

            71 -> { // x == 0
                if (x != 0.0) {
                    index += 1
                }
            }

            73 -> { // last x
                lastX = x
                t = z
                z = y
                y = x
                x = PI
            }

            74 -> { // %
                lastX = x
                x = (x * y) / 100.0
            }
        }
        fonction = 0
    }


    private fun decodDirect(touchei: Int) {

        when (touchei) {
            11 -> { // SST
                if (index < 51) {
                    index += 1
                }
                message = "STEP $index"
                flashMessage(500)
            }

            12 -> { // GOSUB   <-- rtn
                if (runOnOff) {
                    rtn += 1
                    stackRtn[rtn] = (index) // +1 dans l'execution
                    index = prog.codes[index][1]
                    return
                }
            }


            13 -> { // GOTO
                if (runOnOff) {
                    //  Log.d("runindex", "index = ${stackRtn[rtn]}   rtn = $rtn")
                    index = prog.codes[index][1]
                    return
                }
                info2nd = false
                fonction = 6
            }


            14 -> { // f
                fonction = 1
                return
            }

            15 -> { // g
                fonction = 2
                return
            }

            23 -> { // STO
                fonction = 3
                return
            }

            24 -> { // RCL
                fonction = 4
                return
            }

            31 -> { // ENTER
                t = z
                z = y
                y = x
                //  x = x
            }

            32 -> {  // chs()
                lastX = x
                x = -x
            }

            33 -> { // EEX
                fonction = 8

            }

            22 -> { //rot()
                provi = x
                x = y
                y = z
                z = t
                t = provi

            }

            21 -> {  // swap()
                provi = x
                x = y
                y = provi
            }

            25 -> { // sigma+
                message = "maybe later"
                flashMessage()
            }

            51 -> { // add()
                lastX = x
                x = y + x
                y = z
                z = t

            }

            41 -> {  //  sub()
                lastX = x
                x = y - x
                y = z
                z = t
            }

            61 -> { // mul()
                lastX = x
                x = y * x
                y = z
                z = t
            }

            71 -> { // div()
                if (x == 0.0) {
                    message = "division by 0"
                    flashMessage()

                } else {
                    lastX = x
                    x = y / x
                    y = z
                    z = t
                }
            }

            90 -> {
                modePrgm = true
                infoMode = "PRG"
                validDisplay = false
                // fonction = 0
                prog.affPrgm(index)
                return
            }

            34 -> {  // clX()
                x = 0.0
            }

            74 -> {
                runOnOff = !runOnOff
                if (runOnOff) {
                    if (index == 0) {
                        index = 1
                    }
                    run.running()
                } else {
                    message = "RUN OFF"
                    flashMessage(800)
                }

            }

        }

    }

    fun decimalToHMS() {
        lastX = x
        val sign = if (x < 0) -1.0 else 1.0
        val totalSeconds = (x.absoluteValue * 3600.0).roundToLong()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        // Construction du format HH.MMSS
        // On divise les minutes par 100 et les secondes par 10 000
        x = sign * (hours.toDouble() + (minutes / 100.0) + (seconds / 10000.0))
    }

    fun hmsToDecimal() {
        lastX = x
        val sign = if (x < 0) -1.0 else 1.0
        val valAbs = x.absoluteValue
        val hours = floor(valAbs)
        val minutesWithSeconds = (valAbs - hours) * 100.0
        val minutes = floor(minutesWithSeconds) // + 0.5e-15) // Correction de précision
        // truncate(minutesWithSeconds + 0.5e-9)//
        val seconds = (minutesWithSeconds - minutes - 0.5e-9) * 100.0

        x = sign * (hours + (minutes / 60) + (seconds / 3600))
    }


}




