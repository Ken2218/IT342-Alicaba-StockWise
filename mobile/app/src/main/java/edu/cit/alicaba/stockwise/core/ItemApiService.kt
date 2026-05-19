package edu.cit.alicaba.stockwise.core

import edu.cit.alicaba.stockwise.inventory.Item
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ItemApiService {
    @GET("api/v1/items")
    fun getAllItems(): Call<List<Item>>

    @PUT("api/v1/items/{id}")
    fun updateItem(@Path("id") id: Long, @Body item: Item): Call<Item>
    // Create a new item
    @POST("api/v1/items")
    fun createItem(@Body item: Item): Call<Item>

    // Delete an item
    @DELETE("api/v1/items/{id}")
    fun deleteItem(@Path("id") id: Long): Call<Void>
}