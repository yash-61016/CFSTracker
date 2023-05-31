package com.teessideUni.cfs_tracker.presentation.ui.Questioner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.model.QuestionWithOptions
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.QuestionerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionerViewModel @Inject constructor(private val questionnaireRepository: QuestionerRepository) : ViewModel(){
    private val _questions = MutableLiveData<List<QuestionWithOptions>>()
    val questions: LiveData<List<QuestionWithOptions>> = _questions

    init {
        val fetchedQuestions = fetchQuestionsFromDataSource()
        _questions.value = fetchedQuestions
    }

    fun sendAnswers(answers: List<Pair<String, String>>) = viewModelScope.launch {
        Log.d("Answers :", answers.toString())
        questionnaireRepository.sendAnswers(answers).collect{
                result ->
            when(result){
                is Resource.Success -> {
                    // return success
                }
                else -> {}
            }
        }
    }

    private fun fetchQuestionsFromDataSource(): List<QuestionWithOptions> {
        return questionnaireRepository.fetchQuestions()
    }
}