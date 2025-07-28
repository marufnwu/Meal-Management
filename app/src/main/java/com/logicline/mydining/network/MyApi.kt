package com.logicline.mydining.network

import android.util.Base64
import android.util.Log
import com.google.gson.GsonBuilder
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.models.Ad
import com.logicline.mydining.models.Banner
import com.logicline.mydining.models.Fund
import com.logicline.mydining.models.InitiateUser
import com.logicline.mydining.models.Mess
import com.logicline.mydining.models.MessRequest
import com.logicline.mydining.models.MonthOfYear
import com.logicline.mydining.models.OtpRequest
import com.logicline.mydining.models.PurchaseRequest
import com.logicline.mydining.models.Report
import com.logicline.mydining.models.Support
import com.logicline.mydining.models.User
import com.logicline.mydining.models.UserGuide
import com.logicline.mydining.models.response.*
import com.logicline.mydining.utils.LocalDB

import okhttp3.Interceptor
import okhttp3.MultipartBody

import okhttp3.OkHttpClient
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
    ): Call<ServerResponse<User>>

    @GET("api/summary.getHome.php")
    fun getHomeData(
    ): Call<InitialDataResponse>

    @GET("api/user.getInitiatedUsers.php")
    fun getInitiatedUsers(
        @Query("active") active:Int,
        @Query("date") date:String,
    ): Call<UserListResponse>

    @GET("api/user.currentUserInitiated.php")
    fun currentInitiatedUser(
        @Query("date") date:String,
    ): Call<UserListResponse>

    @GET("api/user.getUsers.php")
    fun getUsers(
        @Query("active") active:Int
    ): Call<UserListResponse>

    @FormUrlEncoded
    @POST("api/user.login.php")
    fun login(
        @Field("userName") userName:String,
        @Field("password") password:String,
    ): Call<ServerResponse<CheckLoginResponse>>

    @FormUrlEncoded
    @POST("api/user.addUser.php")
    fun addUser(
        @Field("name") name:String,
        @Field("phone") phone:String,
        @Field("password") password:String,
        @Field("userName") userName:String,
        @Field("email") email:String,
        @Field("city") city:String,
        @Field("gender") gender:String,
        @Field("country") country:String,
    ): Call<GenericRespose>

    @FormUrlEncoded
        @POST("api/mess.create.php")
        fun createMess(
            @Field("messName") messName:String,
            @Field("name") name:String,
            @Field("userName") userName:String,
            @Field("phone") phone:String,
            @Field("password") password:String,
            @Field("email") email:String,
            @Field("country") country:String,
            @Field("city") city:String,
            @Field("gender") gender:String,
        ): Call<ServerResponse<CheckLoginResponse>>

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
    @POST("api/meal.update.php")
    fun updateMeal(
        @Field("mealId") mealId:String,
        @Field("userId") userId:String,
        @Field("date") date:String,
        @Field("breakfast") breakfast: Float,
        @Field("lunch") lunch: Float,
        @Field("dinner") dinner: Float,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/meal.delete.php")
    fun deleteMeal(
        @Field("mealId") mealId:String,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/purchase.add.php")
    fun addPurchase(
        @Field("userId") userId:String,
        @Field("date") date:String,
        @Field("product") product:String,
        @Field("price") price:Int,
        @Field("type") type:Int,
        @Field("isAddAmount") isAddAmount:Int,
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

    @FormUrlEncoded
    @POST("api/user.changeSuperUser.php")
    fun changeSuperUser(
        @Field("newId") newId :String,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/user.checkUserName.php")
    suspend fun isUserNameAvailable(
        @Field("userName") userName :String,
    ): Response<Boolean>

    @GET("api/mess.isUserInitiate.php")
    suspend fun isUserInitiate(): Response<GenericRespose>

    @GET("api/mess.getUsersForInitiate.php")
    suspend fun getUsersForInitiate(): Response<ServerResponse<InitiateUser>>

    @FormUrlEncoded
    @POST("api/mess.initiateUser.php")
    suspend fun initiateUser(
        @Field("userId") userId :String,
    ): Response<GenericRespose>

    @FormUrlEncoded
    @POST("api/mess.initiateAllUser.php")
    fun initiateAllUser(
        @Field("year") year :String,
        @Field("month") month :String,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/user.updadeFcmToken.php")
     fun updadeFcmToken(
        @Field("token") token :String,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/purchase.requestListPurchase.php")
     fun requestListPurchase(
        @Field("productJson") productJson :String,
        @Field("date") date :String,
        @Field("price") price :Float,
        @Field("isDepositToAcc") isDepositToAcc :Int,
        @Field("purchaseType") purchaseType: Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/purchase.requestSinglePurchase.php")
     fun requestSinglePurchase(
        @Field("products") products: String,
        @Field("date") date: String,
        @Field("price") price: Float,
        @Field("isDepositToAcc") isDepositToAcc: Int,
        @Field("purchaseType") purchaseType: Int,

    ): Call<GenericRespose>


    @Multipart
    @POST("api/user.uploadProfileImg.php")
    suspend fun uploadProfileImage(
        @Part pdfFile: MultipartBody.Part,
    ): Response<GenericRespose>

    @FormUrlEncoded
    @POST("api/purchase.getPurchaseRequestManager.php")
    fun getPurchaseRequestManager(
        @Field("year") year :String,
        @Field("month") month :String,
        @Field("status") status :Int,
    ): Call<ServerResponse<List<PurchaseRequest>>>


    @FormUrlEncoded
    @POST("api/purchase.acceptPurchaseRequest.php")
    fun acceptPurchaseRequest(
        @Field("requestId") requestId :Int,
        @Field("isDeposit") isDeposit :Int,
        @Field("purchaseType") purchaseType :Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/purchase.rejectPurchaseRequest.php")
    fun rejectPurchaseRequest(
        @Field("requestId") requestId :Int,
    ): Call<GenericRespose>


    @GET("api/banner.get.php")
    fun getBanner(
        @Query("name") name :String,
    ): Call<ServerResponse<Banner>>

    @FormUrlEncoded
    @POST("api/deposit.getByUserIdDate.php")
    fun getDepositByUserIdDate(
        @Field("userId") userId: String,
        @Field("year") year: String,
        @Field("month") month: String,
        @Field("messId") messId: String,
    ): Call<ServerResponse<DepositHistoryResponse>>


    @FormUrlEncoded
    @POST("api/deposit.delete.php")
    fun deleteDeposit(
        @Field("id") depositId: Int,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/deposit.update.php")
    fun updateDeposit(
        @Field("id") depositId: Int,
        @Field("amount") amount: Float,
        @Field("date") date: String,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/purchase.update.php")
    fun updatePurchase(
        @Field("id") depositId: Int,
        @Field("amount") amount: Float,
        @Field("date") date: String,
        @Field("products") products: String,
        @Field("type") type: Int,

    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/purchase.delete.php")
    fun deletePurchase(
        @Field("id") purchaseId: Int,
        @Field("type") type: Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/user.updateProfile.php")
    fun updateProfile(
        @Field("userId") userId: Int,
        @Field("name") name: String,
        @Field("country") country: String,
        @Field("city") city: String,
        @Field("phone") phone: String,
        @Field("email") email: String,
        @Field("gender") gender: String,
    ): Call<ServerResponse<User>>

    @FormUrlEncoded
    @POST("api/user.deleteCheck.php")
    fun userDeleteCheck(
        @Field("userId") userId: String,
        @Field("year") year: String,
        @Field("month") month: String,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/user.delete.php")
    fun userDelete(
        @Field("userId") userId: String,
        @Field("year") year: String,
        @Field("month") month: String,
    ): Call<GenericRespose>

    @GET("api/helper.getSupport.php")
    fun getSupport(): Call<Support>

    @FormUrlEncoded
    @POST("api/user.requestResetPassword.php")
    fun otpRequest(
        @Field("userName") userName: String,
    ): Call<ServerResponse<OtpRequest>>


    @FormUrlEncoded
    @POST("api/user.veryfyOtp.php")
    fun veryfyOtp(
        @Field("userOtp") userOtp: String,
        @Field("otpId") otpId: String,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/user.resetPassword.php")
    fun resetPassword(
        @Field("otpId") otpId: String,
        @Field("userId") userId: Int,
        @Field("password") password: String,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/mess.reset.php")
    fun resetMess(
        @Field("year") year: String,
        @Field("month") month: String,
    ): Call<GenericRespose>


    @GET("api/ad.settings.php")
    fun getAdSettings(): Call<Ad?>

    @GET("api/guide.getAll.php")
    fun getAllUserGuide(
        @Query("currPage") currPage:Int,
        @Query("totalPage") totalPage:Int
    ) : Call<ServerResponse<Paging<UserGuide>>>
    @GET("api/slider.get.php")
    fun getMainSlider() : Call<ServerResponse<MutableList<UserGuide>>>

    @GET("api/settings.getInitialData.php")
    fun getInitialData(
        @Query("version") version:Int,
    ) : Call<InitialDataResponse>


    @GET("api/mess.changeAlluserAddMeal.php")
    fun changeAlluserAddMeal(
        @Query("status") status:Int,
    ) : Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/mess.updateFund.php")
    fun updateFundStatus(
        @Field("fund") fund:Int,
    ) : Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/fund.get.php")
    fun getFunds(
        @Field("year") year:String,
        @Field("month") month:String,
    ) : Call<ServerResponse<MutableList<Fund>>>


    @FormUrlEncoded
    @POST("api/fund.add.php")
    fun addFund(
        @Field("date") date:String,
        @Field("comment") comment:String,
        @Field("amount") amount: Float,
    ) : Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/fund.update.php")
    fun updateFund(
        @Field("id") id:Int,
        @Field("date") date:String,
        @Field("comment") comment:String,
        @Field("amount") amount: Float,
    ) : Call<GenericRespose>



    @FormUrlEncoded
    @POST("api/fund.delete.php")
    fun deleteFund(
        @Field("id") id:Int,
    ) : Call<GenericRespose>


    @GET("api/mess.getAllReport.php")
    fun getAllReport(
        @Query("currPage") currPage:Int,
        @Query("totalPage") totalPage:Int,
    ) : Call<ServerResponse<Paging<Report>>>


    @GET("api/report.genereteFull.php")
    fun genereteFullReport(
        @Query("year") year:Int,
        @Query("month") month:Int,
    ) : Call<ServerResponse<Report>>
    @FormUrlEncoded
    @POST("api/mess.resetByMonth.php")
    fun resetByMonth(
        @Field("year") year:Int,
        @Field("month") month:Int,
    ) : Call<GenericRespose>
    @FormUrlEncoded
    @POST("api/switchmess.accept.php")
    fun acceptMessMemberJoinRequest(
        @Field("requestId") year:Int,
    ) : Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/switchmess.cancel.php")
    fun cancelMessMemberJoinRequest(
        @Field("requestId") requestId:Int,
    ) : Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/switchmess.request.php")
    fun messSwitchRequest(
        @Field("messId") messId:String,
    ) : Call<GenericRespose>

    @GET("api/switchmess.userJoinHistory.php")
    fun userJoinHistory() : Call<ServerResponse<MutableList<MessRequest>>>

    @GET("api/switchmess.messJoinRequest.php")
    fun messJoinRequest() : Call<ServerResponse<MutableList<MessRequest>>>

    @GET("api/mess.getActiveMonthList.php")
    fun getActiveMonthList() : Call<ServerResponse<MutableList<MonthOfYear>>>

    @GET("api/mess.info.php")
    fun getMessInfo() : Call<ServerResponse<Mess>>

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
                .addInterceptor(TokenInterceptor())

                .addInterceptor(interceptor)
//                .addInterceptor(
//                    com.logicline.mydining.network.ReceivedCookiesInterceptor(
//                        MyApplication.appContext
//                    )
//                )
//                .addInterceptor(
//                    com.logicline.mydining.network.AddCookiesInterceptor(
//                        MyApplication.appContext
//                    )
//                )
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
//                .addInterceptor { chain ->
//                    val original: Request = chain.request()
//                    val requestBuilder: Request.Builder = original.newBuilder()
//                        .addHeader("Authorization", AUTH)
//                        .method(original.method, original.body)
//                    val request: Request = requestBuilder.build()
//                    chain.proceed(request)
//                }
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

    class TokenInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val token = if (LocalDB.getAccessToken()!=null) LocalDB.getAccessToken() else ""
            val userId = if (LocalDB.getUserId()!=null) LocalDB.getUserId() else ""


            return if(!token.isNullOrEmpty()){
                chain.proceed(chain.request()
                    .newBuilder()
                    .header("Authorization","AccessToken $token")
                    .header("Userid","$userId")
                    .build())


            }else{
                chain.proceed(chain.request())
            }
        }

    }


}