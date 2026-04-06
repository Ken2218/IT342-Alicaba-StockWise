package edu.cit.alicaba.stockwise

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// 1. Data Models
data class RegisterRequest(val name: String, val email: String, val password: String)
data class RegisterResponse(val success: Boolean)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean)

// 2. API Endpoints
interface AuthApiService {
    @POST("api/v1/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/v1/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}

// 3. Retrofit Setup (10.0.2.2 is how the Android Emulator reaches your computer's localhost)
object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val authService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}