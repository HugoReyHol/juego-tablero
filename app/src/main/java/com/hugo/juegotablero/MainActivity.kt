package com.hugo.juegotablero

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hugo.juegotablero.model.Casilla

const val ANCHO_LADO: Int = 4

class MainActivity : AppCompatActivity() {
    private val habitaciones: Array<Array<Casilla>> = Array(ANCHO_LADO) {
        Array(ANCHO_LADO) {Casilla()}
    }

    private lateinit var tablero: TextView
    private lateinit var textoActual: TextView
    private lateinit var bNorte: Button
    private lateinit var bOeste: Button
    private lateinit var bEste: Button
    private lateinit var bSur: Button

    private lateinit var casillaActual: Pair<Int, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicializarComponentes()
        inicializarJuego()

    }

    private fun inicializarComponentes() {
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

    private fun inicializarJuego() {
        casillaActual = Pair(0, 0)
        habitaciones.forEach {
            it.forEach {
                it.reiniciar()
            }
        }

        // La entrada siempre está situada arriba a la izquierda
        var inicio: Casilla = habitaciones[casillaActual.first][casillaActual.second]

        inicio.tipo = Casilla.Tipos.ENTRADA
        inicio.explorada = true

        // El dulce se coloca en una habitación aleatoria diferente a la entrada
        var salida: Casilla

        do {
            salida = habitaciones[(0..<ANCHO_LADO).random()][(0..<ANCHO_LADO).random()]

        } while (inicio == salida)

        salida.tipo = Casilla.Tipos.DULCE


        mostrarTablero()
        actualizarBotones()

    }

    private fun mostrarTablero() {
        val t: String = buildString {
            habitaciones.forEachIndexed() { f: Int, columna: Array<Casilla> ->
                columna.forEachIndexed() { c: Int, casilla: Casilla ->
                    if (casillaActual == Pair(f, c)) {
                        append(getString(R.string.persona))

                    } else if (casilla.explorada) {
                        append(casilla.tipo.emoji)

                    } else {
                        append(getString(R.string.sinMirar))

                    }

                }

                append("\n")
            }
        }

        tablero.text = t
        textoActual.text = String.format(
            getString(R.string.textoActual),
            habitaciones[casillaActual.first][casillaActual.second].tipo.emoji
        )

    }

    private fun actualizarBotones(preguntando: Boolean = false) {
        val inactivo: Int = getColor(R.color.inactive)
        val activo: Int = getColor(R.color.active)

        bNorte.isClickable = !preguntando && casillaActual.first != 0
        bOeste.isClickable = !preguntando && casillaActual.second != 0
        bEste.isClickable = !preguntando && casillaActual.second != 3
        bSur.isClickable = !preguntando && casillaActual.first != 3

        bNorte.setBackgroundColor( if (bNorte.isClickable) activo else inactivo)
        bOeste.setBackgroundColor( if (bOeste.isClickable) activo else inactivo)
        bEste.setBackgroundColor( if (bEste.isClickable) activo else inactivo)
        bSur.setBackgroundColor( if (bSur.isClickable) activo else inactivo)

    }

    private fun botonPulsado(boton: Button) {
        when (boton) {
            bNorte -> casillaActual = Pair(casillaActual.first - 1, casillaActual.second)
            bOeste -> casillaActual = Pair(casillaActual.first, casillaActual.second - 1)
            bEste -> casillaActual = Pair(casillaActual.first, casillaActual.second + 1)
            bSur -> casillaActual = Pair(casillaActual.first + 1, casillaActual.second)

        }

        mostrarTablero()

        if (!habitaciones[casillaActual.first][casillaActual.second].explorada) {
            // TODO Si es habitacion vacia hacer pregunta
            actualizarBotones(true)

            // TODO Si es fantasma hacer otra más

            // Si acierta la pregunta o es dulce
            habitaciones[casillaActual.first][casillaActual.second].explorada = true

        }

        actualizarBotones()

        if (habitaciones[casillaActual.first][casillaActual.second].tipo == Casilla.Tipos.DULCE) {
            // TODO código para ganar la partida

        }

    }
}