package com.example.hp33cfr

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.io.File



    class CalculateurStorage(private val context: Context) {
        private val gson = Gson()

        // Noms des fichiers stockés dans l'espace privé de l'application
        private val fichierRegistres = File(context.filesDir, "hp33c_registres.json")
        private val fichierProgramme = File(context.filesDir, "hp33c_programme.json")

        // --- SAUVEGARDE ---
        fun sauvegarderTout(registres: List<Double>, codes: List<List<Int>>) {
            try {
                // 1. Sauvegarde des registres
                val jsonRegistres = gson.toJson(registres)
                fichierRegistres.writeText(jsonRegistres)

                // 2. Sauvegarde du programme
                val jsonProgramme = gson.toJson(codes)
                fichierProgramme.writeText(jsonProgramme)
            } catch (e: Exception) {
                e.printStackTrace() // Log de l'erreur en cas de problème d'écriture
            }
        }

        // --- CHARGEMENT DES REGISTRES ---
        fun chargerRegistres(): MutableList<Double> {
            return if (fichierRegistres.exists()) {
                try {
                    val json = fichierRegistres.readText()
                    val type = object : TypeToken<MutableList<Double>>() {}.type
                    gson.fromJson(json, type)
                } catch (e: Exception) {
                    // En cas d'erreur de lecture, on renvoie une liste propre
                    mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                }
            } else {
                // Premier démarrage : le fichier n'existe pas encore
                mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            }
        }

        // --- CHARGEMENT DU PROGRAMME ---
        fun chargerProgramme(): MutableList<List<Int>> {
            return if (fichierProgramme.exists()) {
                try {
                    val json = fichierProgramme.readText()
                    val type = object : TypeToken<MutableList<List<Int>>>() {}.type
                    gson.fromJson(json, type)
                } catch (e: Exception) {
                    MutableList(53) { listOf(99) }
                }
            } else {
                // Premier démarrage
                MutableList(53) { listOf(99) }
            }
        }



        //EndClass
    }




