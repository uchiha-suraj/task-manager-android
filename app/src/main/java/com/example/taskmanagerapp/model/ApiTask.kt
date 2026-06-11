package com.example.taskmanagerapp.model

data class ApiTask(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val userId: Int = 1
)