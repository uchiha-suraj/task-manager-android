package com.example.taskmanagerapp.network

import com.example.taskmanagerapp.model.ApiTask
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface TaskApi {
    @GET("todos")
    suspend fun getTasks(): Response<List<ApiTask>>

    @POST("todos")
    suspend fun createTask(@Body task: ApiTask): Response<ApiTask>

    @PUT("todos/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: ApiTask): Response<ApiTask>

    @DELETE("todos/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Unit>
}