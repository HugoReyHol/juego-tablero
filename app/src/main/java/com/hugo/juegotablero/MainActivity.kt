package com.hugo.juegotablero

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hugo.juegotablero.model.Casilla
import kotlinx.coroutines.Runnable
import kotlin.math.abs

const val ANCHO_LADO: Int = 4
val PREGUNTAS: Map<String, String> = mapOf(
    "Enunciado1" to "1",
    "Enunciado2" to "2",
    "Enunciado3" to "3",
    "Enunciado4" to "4",
    )

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
    private lateinit var textEnunciado: TextView
    private lateinit var textRespuesta: EditText

    private lateinit var casillaActual: Pair<Int, Int>
    private lateinit var casillaAnterior: Pair<Int, Int>

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

        textEnunciado = findViewById(R.id.textEnunciado)
        textRespuesta = findViewById(R.id.textRespuesta)

        textRespuesta.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
            var presionado: Boolean = false
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                comprobarPregunta()
                presionado = true

            }

            presionado
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
        val inicio: Casilla = habitaciones[casillaActual.first][casillaActual.second]

        inicio.tipo = Casilla.Tipos.ENTRADA
        inicio.preguntasRest = 0

        // El dulce se coloca en una habitación aleatoria diferente a la entrada
        var salida: Casilla

        do {
            salida = habitaciones[(0..<ANCHO_LADO).random()][(0..<ANCHO_LADO).random()]

        } while (inicio == salida)

        salida.tipo = Casilla.Tipos.DULCE

        textEnunciado.visibility = View.INVISIBLE
        textRespuesta.visibility = View.INVISIBLE

        mostrarTablero()
        actualizarBotones()

    }

    private fun mostrarTablero() {
        val t: String = buildString {
            habitaciones.forEachIndexed() { f: Int, columna: Array<Casilla> ->
                columna.forEachIndexed() { c: Int, casilla: Casilla ->
                    if (casillaActual == Pair(f, c)) {
                        append(getString(R.string.persona))

                    } else if (casilla.preguntasRest == 0) {
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

        bNorte.isClickable = !preguntando && casillaActual.first != 0 &&
                habitaciones[casillaActual.first-1][casillaActual.second].tipo != Casilla.Tipos.BLOQUEADO

        bOeste.isClickable = !preguntando && casillaActual.second != 0 &&
                habitaciones[casillaActual.first][casillaActual.second-1].tipo != Casilla.Tipos.BLOQUEADO

        bEste.isClickable = !preguntando && casillaActual.second != 3 &&
                habitaciones[casillaActual.first][casillaActual.second+1].tipo != Casilla.Tipos.BLOQUEADO

        bSur.isClickable = !preguntando && casillaActual.first != 3 &&
                habitaciones[casillaActual.first+1][casillaActual.second].tipo != Casilla.Tipos.BLOQUEADO


        bNorte.setBackgroundColor( if (bNorte.isClickable) activo else inactivo)
        bOeste.setBackgroundColor( if (bOeste.isClickable) activo else inactivo)
        bEste.setBackgroundColor( if (bEste.isClickable) activo else inactivo)
        bSur.setBackgroundColor( if (bSur.isClickable) activo else inactivo)

    }

    private fun botonPulsado(boton: Button) {
        casillaAnterior = casillaActual.copy()

        when (boton) {
            bNorte -> casillaActual = Pair(casillaActual.first - 1, casillaActual.second)
            bOeste -> casillaActual = Pair(casillaActual.first, casillaActual.second - 1)
            bEste -> casillaActual = Pair(casillaActual.first, casillaActual.second + 1)
            bSur -> casillaActual = Pair(casillaActual.first + 1, casillaActual.second)

        }

        mostrarTablero()

        val habitacion: Casilla = habitaciones[casillaActual.first][casillaActual.second]

        if (habitacion.preguntasRest != 0 && habitacion.tipo != Casilla.Tipos.DULCE) preguntar()
        else actualizarBotones()

        // Código para ganar la partida
        if (habitacion.tipo == Casilla.Tipos.DULCE) {
            Toast.makeText(this, "HAS GANADO", Toast.LENGTH_LONG).show()

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                inicializarJuego()
            }, 3000)
        }

    }

    private fun preguntar() {
        actualizarBotones(true)

        val enunciado: String = PREGUNTAS.keys.random()

        textEnunciado.text = enunciado
        textRespuesta.text.clear()
        textEnunciado.visibility = View.VISIBLE
        textRespuesta.visibility = View.VISIBLE

    }

    private fun comprobarPregunta() {
        val respuesta: String = textRespuesta.text.toString().lowercase()
        val habitacion: Casilla = habitaciones[casillaActual.first][casillaActual.second]

        if (respuesta == PREGUNTAS[textEnunciado.text.toString()]) {
            habitacion.preguntasRest -= 1

        } else {
            habitacion.preguntasRest = 0
            habitacion.tipo = Casilla.Tipos.BLOQUEADO
            casillaActual = casillaAnterior
            mostrarTablero()

            comprobarJugable()

        }

        if (habitacion.preguntasRest != 0) preguntar()
        else {
            actualizarBotones()
            textEnunciado.visibility = View.INVISIBLE
            textRespuesta.visibility = View.INVISIBLE

        }
    }

    private fun comprobarJugable() {
        val visitadas: Array<Array<Boolean>> = Array(ANCHO_LADO) {
            Array(ANCHO_LADO) {false}
        }

        val vecinas: ArrayList<Pair<Int, Int>> = arrayListOf(casillaActual.copy())
        var posicion: Pair<Int, Int>
        var encontrado: Boolean = false

        do {
            posicion = vecinas.removeAt(vecinas.lastIndex)
            visitadas[posicion.first][posicion.second] = true
            Log.i("DFS", posicion.toString())
            Log.i("DFS", buildString { visitadas.forEach {
                it.forEach {
                    append("$it, ")
                }
                append("\n") } })

            for (f in -1..1) {
                for (c in -1..1) {
                    // Evita movimientos diagonales
                    if (abs(f) == abs(c)) continue

                    // La nueva coordenada tiene que estar dentro de rango
                    if (posicion.first+f !in 0..<ANCHO_LADO || posicion.second+c !in 0..<ANCHO_LADO) continue

                    // Si la habitacion está bloqueda la salta
                    if (habitaciones[posicion.first+f][posicion.second+c].tipo == Casilla.Tipos.BLOQUEADO) continue

                    // Si ya ha sido visitada la salta
                    if (visitadas[posicion.first+f][posicion.second+c]) continue

                    // Si ya está guardada para visitar no la vuelve guardar
                    if (vecinas.contains(Pair(posicion.first+f, posicion.second+c))) continue

                    // Si la casilla si es valida para seguir buscando
                    if (habitaciones[posicion.first+f][posicion.second+c].tipo == Casilla.Tipos.DULCE) {
                        encontrado = true
                        Log.i("DFS", "encontrado")
                    }

                    vecinas.add(Pair(posicion.first+f, posicion.second+c))
                    Log.i("DFS", "add f${posicion.first+f} c${posicion.second+c}")
                }
            }

            Log.i("DFS", vecinas.toString())

        } while (vecinas.isNotEmpty() && !encontrado)

        // Reinicia el juego si pierdes
        if (!encontrado) {
            Toast.makeText(this, "TE HAS QUEDADO ATRAPADO EN LA CASA", Toast.LENGTH_LONG).show()

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                inicializarJuego()
            }, 3000)
        }

    }
}