package com.example.taskmanagerapp.repository

import com.example.taskmanagerapp.database.TaskDao
import com.example.taskmanagerapp.database.TaskEntity
import com.example.taskmanagerapp.model.ApiTask
import com.example.taskmanagerapp.model.Task
import com.example.taskmanagerapp.network.RetrofitInstance

class TaskRepository(
    private val taskDao: TaskDao
) {

    private val api = RetrofitInstance.api

    suspend fun getRemoteTasks() = api.getTasks()

    suspend fun createRemoteTask(task: ApiTask) = api.createTask(task)

    suspend fun updateRemoteTask(id: Int, task: ApiTask) = api.updateTask(id, task)

    suspend fun deleteRemoteTask(id: Int) = api.deleteTask(id)

    suspend fun getLocalTasks(): List<Task> {
        return taskDao.getTasks().map {
            Task(
                id = it.id,
                title = it.title,
                completed = it.completed
            )
        }
    }

    suspend fun saveTasks(tasks: List<Task>) {
        taskDao.insertTasks(
            tasks.map {
                TaskEntity(
                    id = it.id,
                    title = it.title,
                    completed = it.completed
                )
            }
        )
    }

    suspend fun saveTask(task: Task) {
        taskDao.insertTask(
            TaskEntity(
                id = task.id,
                title = task.title,
                completed = task.completed
            )
        )
    }

    suspend fun updateLocalTask(id: Int, completed: Boolean) {
        taskDao.updateTask(id, completed)
    }

    suspend fun deleteLocalTask(id: Int) {
        taskDao.deleteTask(id)
    }
}