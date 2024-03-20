package com.example.unscramble.ui

data class GameUiState(
    val currentScrambledWord: String = "",
    val isGuessedWordWrong: Boolean = false,
    val userScore: Int = 0,
    val currentGuessOfTen: Int = 0,
    val showFinalScoreDialog: Boolean = false
)
