package com.example.hp33cfr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RunViewModel : ViewModel() {

    val codes = prog.codes

    private var cfrjob: Job? = null
    var  timeexc = 6L

    fun running() {
        cfrjob?.cancel()
        // viewModelScope is tied to the life of this ViewModel
        cfrjob = viewModelScope.launch {
            while (calcul.runOnOff) {
                runCode()
                delay(timeexc)
                if (timeexc > 6L){timeexc = 6L}
            }
        }
    }

    fun runCode() {
        val code = codes[index]
        val longpas = code.count()
        if (code[0] == 99) {
            calcul.runOnOff = false
            return
        }


        rpm(code[0])
        if (code[0] in listOf(12, 13)) {
           return
        }

            if (longpas >= 2) {
                rpm(code[1])
            }
            if (longpas >= 3) {
                rpm(code[2])
            }

        index += 1

    }


    //end class
}
