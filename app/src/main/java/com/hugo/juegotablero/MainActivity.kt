package com.hugo.juegotablero

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val NUM_HABITACIONES: Int = 16

    private val habitaciones: ArrayList<String> = arrayListOf()
    private val explorado: ArrayList<String> = arrayListOf()

    private lateinit var tablero: TextView
    private lateinit var textoActual: TextView
    private lateinit var bNorte: Button
    private lateinit var bOeste: Button
    private lateinit var bEste: Button
    private lateinit var bSur: Button

    private var casillaActual: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        configurarComponentes()
        configurarJuego()

    }

    private fun configurarComponentes() {
        tablero = findViewById(R.id.tablero)
        textoActual = findViewById(R.id.textoActual)

        bNorte = findViewById(R.id.bNorte)
        bOeste = findViewById(R.id.bOeste)
        bEste = findViewById(R.id.bEste)
        bSur = findViewById(R.id.bSur)

        bNorte.setOnClickListener {
            botonPulsado(it as Button)
        }
        bOeste.setOnClickListener {
            botonPulsado(it as Button)
        }
        bEste.setOnClickListener {
            botonPulsado(it as Button)
        }
        bSur.setOnClickListener {
            botonPulsado(it as Button)
        }
    }

    private fun configurarJuego() {
        casillaActual = 0
        habitaciones.clear()
        explorado.clear()

        for (i in 0..<NUM_HABITACIONES) {
            // Todas las habitaciones sin explorar
            explorado.add(getString(R.string.sinMirar))

            // Habitaciones vacías añade fantasmas el 20% de las veces
            habitaciones.add(getString( if ((1..10).random() <= 2) R.string.fantasma else R.string.vacia ))

        }

        // El dulce se coloca en una habitación aleatoria diferente a la entrada
        habitaciones[(1..<NUM_HABITACIONES).random()] = getString(R.string.dulce)

        // La entrada siempre está situada arriba a la izquierda
        habitaciones[0] = getString(R.string.puerta)
        explorado[0] = habitaciones[0]

        mostrarTablero()
        actualizarBotones()

    }

    private fun mostrarTablero() {
        val t: String = buildString {
            for (i in 0..<NUM_HABITACIONES) {
                append(if (i != casillaActual) explorado[i] else getString(R.string.actual))

                if ((i+1)%4 == 0) append("\n")

            }
        }

        tablero.text = t
        textoActual.text = String.format(getString(R.string.textoActual), habitaciones[casillaActual])

    }

    private fun actualizarBotones(preguntando: Boolean = false) {
        val inactivo: Int = getColor(R.color.inactive)
        val activo: Int = getColor(R.color.active)

        bNorte.isClickable = !preguntando && casillaActual > 3
        bOeste.isClickable = !preguntando && casillaActual%4 != 0
        bEste.isClickable = !preguntando && casillaActual%4 != 3
        bSur.isClickable = !preguntando && casillaActual < 12

        bNorte.setBackgroundColor( if (bNorte.isClickable) activo else inactivo)
        bOeste.setBackgroundColor( if (bOeste.isClickable) activo else inactivo)
        bEste.setBackgroundColor( if (bEste.isClickable) activo else inactivo)
        bSur.setBackgroundColor( if (bSur.isClickable) activo else inactivo)

    }

    private fun botonPulsado(boton: Button) {
        when (boton) {
            bNorte -> casillaActual -= 4
            bOeste -> casillaActual--
            bEste -> casillaActual++
            bSur -> casillaActual += 4

        }

        mostrarTablero()

        if (explorado[casillaActual] == getString(R.string.sinMirar)) {
            // TODO Si es habitacion vacia hacer pregunta
            actualizarBotones(true)

            // TODO Si es fantasma hacer otra más

            // Si acierta la pregunta o es dulce
            explorado[casillaActual] = habitaciones[casillaActual]

        }

        actualizarBotones()

        if (explorado[casillaActual] == getString(R.string.dulce)) {
            // TODO código para ganar la partida

        }

    }
}