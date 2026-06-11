package com.example.taskmanagerapp.model

data class Task(
    val id: Int,
    val title: String,
    val completed: Boolean = false
)