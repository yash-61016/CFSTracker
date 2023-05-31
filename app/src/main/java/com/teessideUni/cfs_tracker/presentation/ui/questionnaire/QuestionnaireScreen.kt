package com.teessideUni.cfs_tracker.presentation.ui.questionnaire

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.teessideUni.cfs_tracker.domain.model.QuestionWithOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(navController: NavController, viewModel: QuestionnaireViewModel = hiltViewModel()) {
    val questions by viewModel.questions.observeAsState(emptyList())
    val selectedAnswers = remember { mutableStateListOf<Pair<String, String>>() }
    val listState = rememberLazyListState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Questionnaire") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedAnswers.size == questions.size) {
                        viewModel.sendAnswers(selectedAnswers)
                        navController.navigate("HOME")
                    }

                },
                icon = { Icon(Icons.Filled.Send, "Localized description") },
                text = { Text(text = "Submit") },
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                expanded = listState.isScrollingUp(),
                containerColor = MaterialTheme.colorScheme.primary
            )
        },
    ) {
        it
        if (questions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(top = 120.dp, start = 16.dp, end = 16.dp),
                state = listState
            ) {
                itemsIndexed(questions) { index, questionWithOptions ->
                    MultipleChoiceQuestion(
                        questionWithOptions = questionWithOptions,
                        selectedAnswer = selectedAnswers.getOrNull(index)?.second,
                        onAnswerSelected = { answer ->
                            if (selectedAnswers.size <= index) {
                                selectedAnswers.add(index, questionWithOptions.question to answer)
                            } else {
                                selectedAnswers[index] = questionWithOptions.question to answer
                            }
                        }
                    )
                }
            }
        } else {
            // Handle case when questions are loading or not available
        }
    }
}

@Composable
fun MultipleChoiceQuestion(
    questionWithOptions: QuestionWithOptions,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    Column {
        Text(
            questionWithOptions.question,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        questionWithOptions.options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .selectable(
                        selected = (option == selectedAnswer),
                        onClick = { onAnswerSelected(option) }
                    )
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedAnswer),
                    onClick = { onAnswerSelected(option) }
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

/**
 * Returns whether the lazy list is currently scrolling up.
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

