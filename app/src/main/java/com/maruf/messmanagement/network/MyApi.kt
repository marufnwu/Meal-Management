package com.maruf.messmanagement.network

import android.util.Base64
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.maruf.messmanagement.BuildConfig
import com.maruf.messmanagement.models.response.*
import com.maruf.messmanagement.utils.MyApplication
import okhttp3.JavaNetCookieJar

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.net.CookieHandler
import java.net.CookieManager
import java.util.concurrent.TimeUnit
interface MyApi {

    @GET("api/user.checkLogin.php")
    fun checkLogin(
    ): Call<CheckLoginResponse>

    @GET("api/summary.getHome.php")
    fun getHomeData(
    ): Call<HomeDataResponse>

    @GET("api/user.getUsers.php")
    fun getUsers(
        @Query("active") active:Int
    ): Call<UserListResponse>

    @FormUrlEncoded
    @POST("api/user.login.php")
    fun login(
        @Field("phone") phone:String,
        @Field("password") password:String,
    ): Call<CheckLoginResponse>

    @FormUrlEncoded
    @POST("api/user.addUser.php")
    fun addUser(
        @Field("name") name:String,
        @Field("phone") phone:String,
        @Field("password") password:String,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/meal.get.php")
    fun getMealByMonth(
        @Field("year") year:String,
        @Field("month") month:String,
    ): Call<MealListResponse>

    @FormUrlEncoded
    @POST("api/summary.getMonthSummary.php")
    fun getMonthSummary(
        @Field("year") year:String,
        @Field("month") month:String,
    ): Call<MonthlySummaryResponse>

    @FormUrlEncoded
    @POST("api/meal.getUserMealByDate.php")
    fun getUserMealByDate(
        @Field("userId") userId:String,
        @Field("date") date:String,
    ): Call<UserDayMealResponse>

    @FormUrlEncoded
    @POST("api/purchase.getByDate.php")
    fun getPurchasetByDate(
        @Field("year") year:String,
        @Field("month") month:String,
        @Field("type") type:Int,
    ): Call<PurchaseListResponse>


    @FormUrlEncoded
    @POST("api/meal.add.php")
    fun addMeal(
        @Field("userId") userId:String,
        @Field("date") date:String,
        @Field("breakfast") breakfast: Float,
        @Field("lunch") lunch: Float,
        @Field("dinner") dinner: Float,
    ): Call<UserDayMealResponse>

    @FormUrlEncoded
    @POST("api/purchase.add.php")
    fun addPurchase(
        @Field("userId") userId:String,
        @Field("date") date:String,
        @Field("product") product:String,
        @Field("price") price:Int,
        @Field("type") type:Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/deposit.add.php")
    fun addDeposit(
        @Field("userId") userId:String,
        @Field("date") date:String,
        @Field("amount") amount:Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/deposit.get.php")
    fun getDeposit(
        @Field("year") year:String,
        @Field("month") month:String,
    ): Call<DepositsResponse>


    @FormUrlEncoded
    @POST("api/user.changePassword.php")
    fun changePassword(
        @Field("oldPass") oldPass :String,
        @Field("newPass") newPass :String,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/user.changeManager.php")
    fun changeManager(
        @Field("newId") newId :String,
        @Field("value") value :Int,
    ): Call<GenericRespose>

    companion object {
        @Volatile
        private var myApiInstance: MyApi? = null
        private val LOCK = Any()

        operator fun invoke() = myApiInstance ?: synchronized(LOCK) {
            myApiInstance ?: createClient().also {
                myApiInstance = it
            }
        }


        private fun createClient(): MyApi {
            val AUTH: String = "Basic ${
                Base64.encodeToString(
                    ("${BuildConfig.USER_NAME}:${BuildConfig.USER_PASSWORD}").toByteArray(),
                    Base64.NO_WRAP
                )
            }"



            val interceptor = run {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                }
            }


            var cookieHandler: CookieHandler = CookieManager()

            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                //.cookieJar(JavaNetCookieJar(cookieHandler))
                .readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .callTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .addInterceptor(ReceivedCookiesInterceptor(MyApplication.appContext))
                .addInterceptor(AddCookiesInterceptor(MyApplication.appContext))
                .addInterceptor { chain ->
                    try {
                        val request = chain.request()
                        val response = chain.proceed(request)

                        response
                    }catch (e :Exception){
                        e.message?.let { Log.d("OkHttpError", it) }
                        chain.proceed(chain.request())
                    }
                }
                .addInterceptor { chain ->
                    val original: Request = chain.request()
                    val requestBuilder: Request.Builder = original.newBuilder()
                        .addHeader("Authorization", AUTH)
                        .method(original.method, original.body)
                    val request: Request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()

            val gsonBuilder = GsonBuilder()
            gsonBuilder.setLenient()
            val gson = gsonBuilder.create()

            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(MyApi::class.java)
        }


    }


}