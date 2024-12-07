package com.hugo.juegotablero.model

class Casilla{
    var preguntasRest: Int = 1
    var tipo: Tipos = Tipos.VACIO

    enum class Tipos(val emoji: String) {
        ENTRADA("\uD83D\uDEAA"),
        DULCE("\uD83C\uDF6D"),
        VACIO("◻\uFE0F"),
        FANTASMA("\uD83D\uDC7B"),
        BLOQUEADO("\uD83D\uDFE5")
    }

    fun reiniciar() {
        tipo = if ((1..10).random() <= 2) Tipos.FANTASMA else Tipos.VACIO
        preguntasRest = if (tipo == Tipos.VACIO) 1 else 2
    }

}