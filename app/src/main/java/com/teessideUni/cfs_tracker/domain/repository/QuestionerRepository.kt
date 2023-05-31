package com.teessideUni.cfs_tracker.domain.repository

import com.teessideUni.cfs_tracker.domain.model.QuestionWithOptions
import com.teessideUni.cfs_tracker.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface QuestionerRepository {
    fun fetchQuestions(): List<QuestionWithOptions>
    fun sendAnswers(answers: List<Pair<String, String>>): Flow<Resource<Boolean>>
}