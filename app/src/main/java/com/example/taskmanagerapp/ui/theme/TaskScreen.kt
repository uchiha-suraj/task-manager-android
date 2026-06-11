package com.example.taskmanagerapp.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagerapp.model.ApiTask
import com.example.taskmanagerapp.model.Task
import com.example.taskmanagerapp.network.RetrofitInstance
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagerapp.common.UiState


@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel = viewModel()
) {
    val taskUiState = taskViewModel.taskUiState
    var taskText by remember { mutableStateOf("") }
    val tasks = taskViewModel.tasks

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Task Manager",
            fontSize = 28.sp
        )

        Text(
            text = "Total: ${tasks.size} | Completed: ${tasks.count { it.completed }}",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        when (taskUiState) {
            is UiState.Loading -> {
                Text(
                    text = "Loading tasks...",
                    fontSize = 16.sp
                )
            }
            is UiState.Success -> {
                // Show success state
            }
            is UiState.Error -> {
                Text(
                    text = taskUiState.message,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            TextField(
                value = taskText,
                onValueChange = { taskText = it },
                placeholder = { Text("Enter task") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    taskViewModel.addTask(taskText)
                    taskText = ""
                }
            ) {
                Text("Add")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (tasks.isEmpty()) {
            Text(
                text = "No tasks yet",
                fontSize = 18.sp
            )
        } else {
            LazyColumn {
                itemsIndexed(tasks) { index, task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.completed,
                                onCheckedChange = { isChecked ->
                                    taskViewModel.updateTask(task, isChecked)
                                }
                            )

                            Text(
                                text = task.title,
                                fontSize = 20.sp,
                                textDecoration = if (task.completed) {
                                    TextDecoration.LineThrough
                                } else {
                                    TextDecoration.None
                                },
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    taskViewModel.deleteTask(task)
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
