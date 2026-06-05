package com.example.hp33cfr

import android.os.Handler
import android.os.Looper



class Prgm {
    // Initialisation avec une valeur par défaut à l'index 0
    var codes = mutableListOf<List<Int>>(listOf(99))

    init {
        codes.clear()
        for (i in 0..52) {
            codes.add(listOf(99))
        }
    }

    private var info2nd = false
    private var sgn =  false
    private var previous = 99
    private var fonction = 0

    fun prgm(touchei: Int) {

        // RETOUR MODE RUN
        if (touchei == 90) {
            modePrgm = false
            infoMode = "RUN"
            index = 1
            validDisplay = true
            formatAff()
            return
        }

// BST
        if (touchei == 11 && fonction == 2) {
            if (index >= 2) {
                affPrgm(index - 1)
                index -= 1
                fonction = 0
                return
            } else
                fonction = 0
            return
        }

// SST
        if (touchei == 11 && fonction == 0) {
            if (index < 52) {
                index += 1
                affPrgm(index)
                return
            }
        }


// RESET
        if (fonction == 1 && touchei == 32) {
            clearPrgm()
            return
        }

        when (fonction) {
            0 -> foncO(touchei)
            1 -> prgF(touchei)
            2 -> prgG(touchei)
            3 -> prgSTO(touchei)
            4 -> prgRCL(touchei)
            5 -> prgGSB(touchei)
            6 -> prgGTO(touchei)
            8 -> prgEEX(touchei)

        }
    }

    private fun prgEEX(touchei: Int) {

        if (touchei == 41) { sgn = true }
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

                    codes[index] = if(sgn) {listOf(33, 41 , previous)} else {listOf(33,51, previous)}
                    fonction = 0
                    info2nd = false
                    sgn = false
                    prevAff()
                    return
//                } else {
//                    message = "error eex"
//                    flashMessage(1000L)
//                    info2nd = false
//                    fonction = 0
//                    return
//                }
            }
        }
    }

    private fun foncO(touchei: Int) {
        when (touchei) {
            14 -> {//infoF = true
                fonction = 1
                return
            }

            15 -> {//infoG = true
                fonction = 2
                return
            }

            23 -> {//infoSTO = true
                fonction = 3
                return
            }

            24 -> {//infoRCL = true
                fonction = 4
                return
            }

            12 -> {//infoGSB = true
                fonction = 5
                return
            }

            13 -> {//infoGTO = true
                fonction = 6
                return
            }
            33 -> { // info EEX

                fonction = 8
                return
            }

        }
        //  memo code
        codes[index] = listOf(touchei)
        prevAff()
    }

    private fun prevAff() {
        if (index < 52) {
            index += 1
        }
        affPrgm(index - 1)

    }

    // GOTO
    private fun prgGTO(touchei: Int) {
        val codegto = 13
        vargto(touchei, codegto)
        return
    }


    // GESTION GO SUB
    private fun prgGSB(touchei: Int) {
        val nbGOSUB = calculGSB()
        if (nbGOSUB < 3) {
            val codegto = 12
            vargto(touchei, codegto)
            return
        } else {
            message = "max 3 GOSUB"
            flashMessage(950)
            fonction = 0
            return
        }
    }

    private fun vargto(touchei: Int, codegto: Int) {
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
                    codes[index] = listOf(codegto, previous)
                    fonction = 0
                    info2nd = false
                    prevAff()
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


    private fun prgRCL(touchei: Int) {
        codes[index] = listOf(24, touchei)
        fonction = 0
        prevAff()
    }

    private fun prgSTO(touchei: Int) {
        if (!info2nd && touchei in 0..7) {
            codes[index] = listOf(23, touchei)
            fonction = 0
            prevAff()
            return
        }
        if (touchei in listOf(41, 51, 61, 71)) {
            info2nd = true
            previous = touchei
            return
        }

        if (info2nd) {
            codes[index] = listOf(23, previous, touchei)
            fonction = 0
            info2nd = false
            prevAff()
            return
        }
    }

    private fun prgG(touchei: Int) {
        codes[index] = listOf(15, touchei)
        fonction = 0

        prevAff()

    }

    private fun prgF(touchei: Int) {
        if (touchei == 31) {
            message = "nop"
            flashMessage(1000)
            fonction = 0
            return
        } else
            codes[index] = listOf(14, touchei)
        fonction = 0
        prevAff()
    }


    private fun clearPrgm() {
        fonction = 0
        index = 1
        info2nd = false
        codes.clear()
        for (i in 0..52) {
            codes.add(listOf(99))
        }
        affPrgm(1)
    }


    fun affPrgm(step: Int) {

        var predisplay = ""
        val longligne = codes[step].count()
        for (i in 0 until (longligne)) {
            predisplay += codes[step][i].toString().padStart(2, '0') + " "
        }
        val stringSteep = step.toString().padStart(2, '0')
        display = "$stringSteep -- $predisplay"

    }

    private fun flashMessage(duree: Long) {
        display = message

        Handler(Looper.getMainLooper()).postDelayed({
            affPrgm(index)
        }, duree) // 10000 millisecondes = 10 secondes
    }


    private fun calculGSB(): Int {
        var j = 0
        for (i in 0..<codes.count()) {
            if (codes[i][0] == 12) {
                j += 1
            }
        }
        return j
    }


    // END CLASS
}



