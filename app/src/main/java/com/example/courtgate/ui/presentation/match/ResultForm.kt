package com.example.courtgate.ui.presentation.match

import com.example.courtgate.domain.models.LastResult

const val SETS_PER_MATCH = 3
private const val MAX_GAMES_PER_SET = 7

enum class Team { First, Second }

// Los juegos se guardan como texto mientras se escriben: un campo vacío no es lo mismo que un 0
data class SetScoreInput(
    val player1: String = "",
    val player2: String = ""
) {
    val isEmpty: Boolean get() = player1.isEmpty() && player2.isEmpty()
    val isComplete: Boolean get() = player1.isNotEmpty() && player2.isNotEmpty()
}
//TODO: explorar las property, data class y todo lo que haga que la screen no sea lo más "tonta" posible.
// Este código no tiene el humbral de calidad del resto hecho por mí

// id = 0: resultado nuevo (Room genera el id al insertar)
data class ResultForm(
    val id: Int = 0,
    val sets: List<SetScoreInput> = List(SETS_PER_MATCH) { SetScoreInput() }
) {
    val isNew: Boolean get() = id == 0

    fun withScore(setIndex: Int, team: Team, value: String): ResultForm {
        val updated = sets.mapIndexed { index, set ->
            when {
                index != setIndex -> set
                team == Team.First -> set.copy(player1 = value)
                else -> set.copy(player2 = value)
            }
        }
        return copy(sets = updated)
    }
}

enum class ResultFormError {
    MissingSets,        // Faltan los dos primeros sets o un set está a medias
    InvalidGames,       // Más juegos de los posibles en un set
    TiedSet,            // Un set no puede acabar en empate
    ThirdSetNotNeeded   // Ganar los dos primeros sets cierra el partido
}

// El tercer set es opcional. Se guarda como 0-0 si no se juega (así lo interpreta LastResultCard)
fun ResultForm.validate(): ResultFormError? {
    val (set1, set2, set3) = sets
    if (!set1.isComplete || !set2.isComplete) return ResultFormError.MissingSets
    if (!set3.isEmpty && !set3.isComplete) return ResultFormError.MissingSets

    val played = sets.filter { it.isComplete }
    val games = played.map { it.player1.toInt() to it.player2.toInt() }
    if (games.any { (p1, p2) -> p1 > MAX_GAMES_PER_SET || p2 > MAX_GAMES_PER_SET }) {
        return ResultFormError.InvalidGames
    }
    if (games.any { (p1, p2) -> p1 == p2 }) return ResultFormError.TiedSet

    val firstSetWinner = games[0].first > games[0].second
    val secondSetWinner = games[1].first > games[1].second
    if (set3.isComplete && firstSetWinner == secondSetWinner) {
        return ResultFormError.ThirdSetNotNeeded
    }
    return null
}

// Llamar solo con un formulario válido
fun ResultForm.toLastResult(): LastResult {
    fun SetScoreInput.games() = (player1.toIntOrNull() ?: 0) to (player2.toIntOrNull() ?: 0)
    val (set1, set2, set3) = sets.map { it.games() }
    return LastResult(
        id = id,
        player1Set1 = set1.first,
        player2Set1 = set1.second,
        player1Set2 = set2.first,
        player2Set2 = set2.second,
        player1Set3 = set3.first,
        player2Set3 = set3.second
    )
}

fun LastResult.toForm(): ResultForm {
    val thirdSetPlayed = player1Set3 != 0 || player2Set3 != 0
    return ResultForm(
        id = id,
        sets = listOf(
            SetScoreInput(player1Set1.toString(), player2Set1.toString()),
            SetScoreInput(player1Set2.toString(), player2Set2.toString()),
            if (thirdSetPlayed) SetScoreInput(player1Set3.toString(), player2Set3.toString())
            else SetScoreInput()
        )
    )
}
