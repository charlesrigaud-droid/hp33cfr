package com.example.hp33cfr


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.hp33cfr.MainActivity.Global.mesRegistres
import com.example.hp33cfr.MainActivity.Global.monProgramme


class MainActivity : ComponentActivity() {
    lateinit var storage: CalculateurStorage

    //  données sauvées
    object Global {
        lateinit var mesRegistres: MutableList<Double>
        lateinit var monProgramme: MutableList<List<Int>>
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SourceLockedOrientationActivity")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        enableEdgeToEdge()

        setContent {
            Aff()
        }
        storage = CalculateurStorage(this)
        mesRegistres = storage.chargerRegistres()
        monProgramme = storage.chargerProgramme()
    }


    override fun onStop() {
        super.onStop()
        // sauvegarde
        storage.sauvegarderTout(calcul.registres, prog.codes)
    }

}

var infomoi = ""
var Nenter = 0.12f
var X = 0f
var Y = 0f
var I = 0

var display by mutableStateOf("0.0")
var infoMode by mutableStateOf("RUN")

@SuppressLint("UseOfNonLambdaOffsetOverload")
@OptIn(ExperimentalMaterial3Api::class)


@Composable
fun Aff() {
    val context = LocalContext.current
    var showStack by remember { mutableStateOf(false) }
    var showMem by remember { mutableStateOf(false) }

    val infotext = buildAnnotatedString {
        append("HP33_CFR ")
        withStyle(style = SpanStyle(color = Color.Magenta, fontSize = 16.sp)) {
            append("V 3.0")
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = infotext) },
                navigationIcon = {
                    IconButton(onClick = { showStack = true }) {
                        Icon(
                            painter = painterResource(R.drawable.icstack),
                            contentDescription = null
                        )
                    }
                    ListStack(expanded = showStack, onDismiss = { showStack = false })
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }

                    // La Box s'ouvre ici
                    Box(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        // 1. Le Texte cliquable
                        Text(
                            text = "Notices",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Red,
                            fontSize = 20.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier
                                .clickable { showMenu = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )

                        // 2. Le menu déroulant
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Notice 1") },
                                onClick = {
                                    showMenu = false
                                    openPdfFromAssets(context, "notice25.pdf")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Notice 2") },
                                onClick = {
                                    showMenu = false
                                    openPdfFromAssets(context, "ohpg.pdf")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Notice 3") },
                                onClick = {
                                    showMenu = false
                                    openPdfFromAssets(context, "apps.pdf")
                                }
                            )
                        }
                    } // La Box se ferme ICI, englobant bien ses deux enfants

                    Box {
                        IconButton(onClick = { showMem = true }) {
                            Icon(
                                painter = painterResource(R.drawable.icmem),
                                contentDescription = "memory"
                            )
                        }
                        ShowMem(expanded = showMem, onDismiss = { showMem = false })
                    }
                }
            )
        },

        content = { innerPadding -> // Utilisation obligatoire du padding
            var imageWidthDp by remember { mutableStateOf(0.dp) }
            var imageHeightDp by remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current


            // appliquer "innerPadding" sur le conteneur principal du contenu ici !
            Box(
                modifier = Modifier
                    .padding(innerPadding), // Évite que l'image soit cachée par le TopAppBar

            ) {
                Image(
                    painter = painterResource(id = R.drawable.hp33),
                    contentDescription = null,
                    contentScale = ContentScale.Fit, //  la largeur
                    modifier = Modifier
                        .onGloballyPositioned { coords ->
                            imageWidthDp = with(density) { coords.size.width.toDp() }
                            imageHeightDp = with(density) { coords.size.height.toDp() }
                            //  Récupération de la taille en pixels dès que l'image est placée

                            //  Log.d("image", "Taille de l'image : $imageWidthDp x $imageHeightDp")
                        }
                )


// ---      BOX AFFICHAGE    X     ---
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth(0.74f) // Taille affichage
                        .fillMaxHeight(0.075f)
                        .offset(
                            x = 0.005 * imageWidthDp,
                            y = 0.142 * imageHeightDp
                        ), // position relatives
                    // .border(1.dp, color = Color.Red),
                    contentAlignment = Alignment.CenterEnd // Aligne le contenu au centre-droit de la Box
                ) {
                    Text(
                        text = display,
                        color = Color(0xFFFF0000), // Rouge LED
                        modifier = Modifier.padding(end = 10.dp), // Petite marge à droite pour ne pas coller au bord
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontSize = 40.sp,
                            color = Color(0xFFFF0000),
                            shadow = Shadow(color = Color.Red, blurRadius = 8f)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .matchParentSize() // S'adapte à la taille de l'image

                )

                {
                    // bouto RUN/PRGM
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.17f)
                            .fillMaxHeight(0.045f)
                            .offset(0.599 * imageWidthDp, 0.239 * imageHeightDp)
                            // .border(1.dp, color = Color.Yellow)
                            .clickable(
                                onClick = {
                                    if (index == 0) {
                                        index = 1
                                    }
                                    rpm(90)
                                }
                            )
                    )
                    {
                        Text(
                            text = infoMode,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = TextStyle(
                                fontFamily = FontFamily.Default,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        )
                    }


                    // Box Info +
                    Box(
                        modifier = Modifier

                            .fillMaxWidth(0.9f) // Taille affichage
                            .fillMaxHeight(0.075f)
                            .offset(
                                x = 0.045 * imageWidthDp,
                                y = .04 * imageHeightDp
                            ),
                        //.border(1.dp, color = Color.Red),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(
                            text = infomoi,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),

                            style = TextStyle(
                                fontSize = 27.sp,
                                color = Color.White
                            )
                        )
                    }


// Mise en place clavier clickable

                    // for (touche in keyscode)
                    keyscode.forEach { touche ->
                        X = touche[1].toFloat() / 1000 //  % de la largeur
                        Y = touche[2].toFloat() / 1000 //  % de la hauteur
                        I = touche[0]
                        //  Log.d("touche", "Touche  $I ,   $X ,    $Y")
                        ToucheI(X * imageWidthDp, Y * imageHeightDp, I)
                    }
                }

            }
        }
    )
}


@Composable
fun ToucheI(posX: Dp, posY: Dp, touchei: Int) {

    Nenter = if (touchei == 31) {
        0.280f
    } else {
        0.12f
    }


    Box(
        modifier = Modifier
            .fillMaxWidth(Nenter) // Taille de la zone de clic
            .fillMaxHeight(0.06f)
            .offset(x = posX, y = posY) // Décalage relatif
            // .background(Color.Red.copy(alpha = 0.3f)) //couleur debug
            .clickable(
                onClick = { rpm(touchei) }
            )
    )
}


//
//@Preview(
//    showBackground = true, showSystemUi = false,
//    device = "spec:parent=pixel_5,navigation=buttons"
//)
//@Composable
//fun AffPreview() {
//
//    Aff()
//
//}

