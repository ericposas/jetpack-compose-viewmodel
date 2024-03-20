package com.example.unscramble.ui

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val TAG: String = "GameViewModel.kt"

class GameViewModel : ViewModel() {
    val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var currentWord: String

    var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess: MutableState<String> = mutableStateOf("")

    init {
        resetGame()
    }

    fun showFinalScoreDialog() {
        _uiState.update { state ->
            state.copy(showFinalScoreDialog = true)
        }
    }

    private fun incrementGuessCount() {
        var _guessCount = _uiState.value.currentGuessOfTen
        if (_guessCount < 10) {
            _guessCount++
            _uiState.update { state ->
                state.copy(currentGuessOfTen = _guessCount)
            }
        }
    }

    private fun increaseUserScore() {
        var _updatedScore = _uiState.value.userScore
        _updatedScore++
        _uiState.update { state ->
            state.copy(userScore = _updatedScore)
        }
        Log.i(TAG, "increaseUserScore() called")
    }

    private fun decreaseUserScore() {
        var _updatedScore = _uiState.value.userScore
        _updatedScore--
        if (_updatedScore > -1) {
            _uiState.update { state ->
                state.copy(userScore = _updatedScore)
            }
        }
    }

    fun nextTurnNewWord() {
        _uiState.update { state ->
            state.copy(currentScrambledWord = pickRandomWordAndShuffle())
        }
    }

    fun checkUserGuess() {
        if (userGuess.value.equals(currentWord, ignoreCase = true)) {
            if (_uiState.value.currentGuessOfTen == 9) {
                Log.i(TAG, "You win!!")
                _uiState.update { state ->
                    state.copy(
                        isGuessedWordWrong = false,
                        showFinalScoreDialog = true
                    )
                }
            } else {
                Log.i(TAG, "Correct!")
                _uiState.update { currentState ->
                    currentState.copy(isGuessedWordWrong = false)
                }
                increaseUserScore()
                incrementGuessCount()
                nextTurnNewWord()
            }
        } else {
            Log.i(TAG, "You loser!")
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
            decreaseUserScore()
            incrementGuessCount()
        }
        updateGuessValue("")
    }

    fun updateGuessValue(newGuess: String): Unit {
        userGuess.value = newGuess
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        var _word = word.toCharArray()
        _word.shuffle()
        while (String(_word) == word) {
            _word.shuffle()
        }
        return String(_word)
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value =
            GameUiState(currentScrambledWord = pickRandomWordAndShuffle(), userScore = 0)
    }

}