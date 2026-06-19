package com.example.hp33cfr


val calcul = Execut()
val prog = Prgm()
val run = RunViewModel()

var fixSciEng = 1 //  fix = 0  ,  Sci = 1 , Eng = 2
var modePrgm: Boolean = false
var validDisplay = true
var saisie = ""
var message = ""
var tempString = ""
var modeaff: Int = 0 // affichage 0 -> x , 1 -> saisie , 2  -> message
var fix = 12

var xEEX = ""
var index = 1 // pointeur de prog
fun rpm(touchei: Int) {
    // Log.d("rpm", "rpm $touchei")

    if (modePrgm) {
        prog.prgm(touchei) // mode programmation
    } else
        if (calcul.fonction != 0) {
            calcul.execut(touchei)
            modeaff = 0
            formatAff()

        } else
            if ((touchei in 0..9) || touchei == 73) {

                // affichage de saisie

                modeaff = 1
                val testpoint = saisie.contains('.')

                saisie += if (touchei == 73 && !testpoint) {
                    "."
                } else {
                    touchei.toString()
                }
                tempString = saisie
                formatAff()

            } else {
                saisie = ""
                calcul.execut(touchei)
                modeaff = 0
                formatAff()

            }
}


fun formatAff() {
    if (validDisplay) {

        display = when (modeaff) {
            0 -> {
                affSmart()

            }

            1 -> {
                saisie
            }


            else -> {
                "PB"
            }
        }
    }


}






