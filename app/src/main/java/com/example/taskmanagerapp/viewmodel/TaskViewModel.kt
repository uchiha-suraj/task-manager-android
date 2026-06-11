package com.example.taskmanagerapp.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.taskmanagerapp.model.Task
import androidx.lifecycle.viewModelScope
import com.example.taskmanagerapp.common.UiState
import com.example.taskmanagerapp.database.TaskDatabase
import kotlinx.coroutines.launch
import com.example.taskmanagerapp.model.ApiTask
import com.example.taskmanagerapp.repository.TaskRepository

class TaskViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val taskDao = TaskDatabase
        .getDatabase(application)
        .taskDao()

    private val repository = TaskRepository(taskDao)
    var taskUiState by mutableStateOf<UiState<List<Task>>>(UiState.Loading)
        private set

    val tasks = mutableStateListOf<Task>()

    fun loadTasks() {
        viewModelScope.launch {
            val localTasks = repository.getLocalTasks()

            if (localTasks.isNotEmpty()) {
                tasks.clear()
                tasks.addAll(localTasks)
                taskUiState = UiState.Success(tasks)
            } else {
                taskUiState = UiState.Loading
            }

            try {
                val response = repository.getRemoteTasks()

                if (response.isSuccessful) {
                    val apiTasks = response.body() ?: emptyList()
                    taskUiState = UiState.Success(tasks)

                    tasks.clear()

                    val mappedTasks = apiTasks.take(10).map {
                        Task(
                            id = it.id,
                            title = it.title,
                            completed = it.completed
                        )
                    }
                    tasks.clear()
                    tasks.addAll(mappedTasks)

                    repository.saveTasks(mappedTasks)

                    taskUiState = UiState.Success(tasks)
                } else {
                    taskUiState = UiState.Error("Failed to load tasks")
                }

            } catch (e: Exception) {
                taskUiState = UiState.Error("Something went wrong: ${e.message}")
            }
        }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return

        viewModelScope.launch {
            taskUiState = UiState.Loading
            try {
                val newTask = ApiTask(
                    id = 0,
                    title = title,
                    completed = false,
                    userId = 1
                )

                val response = repository.createRemoteTask(newTask)

                if (response.isSuccessful) {
                    val createdTask = response.body()
                    taskUiState = UiState.Success(tasks)
                    if (createdTask != null) {
                        val task = Task(
                            id = createdTask.id,
                            title = createdTask.title,
                            completed = createdTask.completed
                        )

                        tasks.add(task)

                        repository.saveTask(task)

                        taskUiState = UiState.Success(tasks)
                    }
                } else {
                    taskUiState = UiState.Error("Failed to add task")
                }

            } catch (e: Exception) {
                taskUiState = UiState.Error("Something went wrong: ${e.message}")
            }
        }
    }

    fun updateTask(task: Task, completed: Boolean) {
        viewModelScope.launch {
            try {
                taskUiState = UiState.Loading
                val updatedApiTask = ApiTask(
                    id = task.id,
                    title = task.title,
                    completed = completed,
                    userId = 1
                )

                val response = repository.updateRemoteTask(
                    id = task.id,
                    task = updatedApiTask
                )

                if (response.isSuccessful) {
                    val updatedTask = response.body()
                    taskUiState = UiState.Success(tasks)
                    if (updatedTask != null) {
                        val index = tasks.indexOfFirst {
                            it.id == task.id
                        }

                        if (index != -1) {
                            tasks[index] = Task(
                                id = updatedTask.id,
                                title = updatedTask.title,
                                completed = updatedTask.completed
                            )
                            repository.updateLocalTask(
                                id = task.id,
                                completed = completed
                            )
                        }
                    }
                } else {
                    taskUiState = UiState.Error("Failed to update task")
                }

            } catch (e: Exception) {
                taskUiState = UiState.Error("Something went wrong: ${e.message}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskUiState = UiState.Loading
                val response = repository.deleteRemoteTask(task.id)

                if (response.isSuccessful) {
                    taskUiState = UiState.Success(tasks)
                    tasks.removeAll {
                        it.id == task.id
                    }
                    repository.deleteLocalTask(task.id)
                } else {
                    taskUiState = UiState.Error("Failed to delete task")
                }

            } catch (e: Exception) {
                taskUiState = UiState.Error("Something went wrong: ${e.message}")
            }
        }
    }
}
