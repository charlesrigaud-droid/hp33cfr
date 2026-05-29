package com.example.hp33cfr

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.content.FileProvider
import java.io.File

fun flashMessage(duree: Long = 2000) {
    validDisplay = false
    modeaff = 2
    saisie = ""
    display = message

    Handler(Looper.getMainLooper()).postDelayed({
        modeaff = 0
        saisie = ""
        validDisplay = true
        formatAff()
    }, duree) // 10000 millisecondes = 10 secondes
}


@Composable
fun ListStack(expanded: Boolean, onDismiss: () -> Unit) {
    val infotrigo:String = when (calcul.degRadGrad){
        1 -> "deg"
        2 -> "rad"
        3 -> "grad"
        else -> {
            "xxx"
        }
    }

    //val tText = " T : " + calcul.t.toString() + "   \\     $infotrigo"
    // On construit le texte du registre T avec un style spécifique pour infotrigo
    val tText = buildAnnotatedString {
        append(" T : ${calcul.t}    ||     ") // Partie normale

        withStyle(style = SpanStyle(color = Color.Red)) {
            append(infotrigo) // Partie en rouge
        }
    }
    val zText = " Z : ${calcul.z}"
    val yText = " Y : ${calcul.y}"
    val xText = " X : ${calcul.x}"


    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss // 4. Ferme le menu quand on clique ailleurs
    ) {
        DropdownMenuItem(
            text = { Text(tText ) }, // + infotrigo
            onClick = { onDismiss() } /* Non cliquable : on ne fait rien ou on ferme */
        )
        DropdownMenuItem(
            text = { Text(zText) },
            onClick = { onDismiss() }
        )
        DropdownMenuItem(
            text = { Text(yText) },
            onClick = { onDismiss() }
        )
        DropdownMenuItem(
            text = { Text(xText) },
            onClick = { onDismiss() }
        )
    }
}


@Composable
fun ShowMem(expanded: Boolean, onDismiss: () -> Unit) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss // 4. Ferme le menu quand on clique ailleurs
    ) {
        DropdownMenuItem(
            text = { Text("RCL0 : \n ${calcul.registres[0]}") },
            onClick = { onDismiss() } /* Non cliquable : on ne fait rien ou on ferme */
        )
        DropdownMenuItem(
            text = { Text("RCL1 :  \n ${calcul.registres[1]}") },
            onClick = { onDismiss() }
        )
        DropdownMenuItem(
            text = { Text("RCL2 :  \n ${calcul.registres[2]}")},
            onClick = { onDismiss() }
        )
        DropdownMenuItem(
            text = { Text("RCL3 :  \n ${calcul.registres[3]}") },
            onClick = { onDismiss() }
        )
        DropdownMenuItem(
            text = { Text("RCL4 :  \n ${calcul.registres[4]}") },
            onClick = { onDismiss() }
        )
        DropdownMenuItem(
            text = { Text("RCL5 :  \n ${calcul.registres[5]}") },
            onClick = { onDismiss() }
        )
        DropdownMenuItem(
            text = { Text("RCL6 :   \n ${calcul.registres[6]}") },
            onClick = { onDismiss() }
        )
        DropdownMenuItem(
            text = { Text("RCL7 :  \n ${calcul.registres[7]}") },
            onClick = { onDismiss() }
        )
    }
}

fun openPdfFromAssets(context: Context, fileName: String) {
    try {
        // 1. Copier le fichier des assets vers le cache interne
        val file = File(context.cacheDir, fileName)
        if (!file.exists()) {
            context.assets.open(fileName).use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        // 2. Obtenir l'URI via FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // 3. Créer l'Intent
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) // Évite de garder le PDF dans le stack
        }

        context.startActivity(Intent.createChooser(intent, "Ouvrir la notice avec..."))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

