package com.logicline.mydining.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.data.enums.PurchaseRequestStatus
import com.logicline.mydining.data.models.Ad
import com.logicline.mydining.data.models.Banner
import com.logicline.mydining.data.models.Country
import com.logicline.mydining.data.models.Deposit
import com.logicline.mydining.data.models.DepositHistory
import com.logicline.mydining.data.models.Fund
import com.logicline.mydining.data.models.Meal
import com.logicline.mydining.data.models.MealsData
import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.MessRequest
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.models.MonthOfYear
import com.logicline.mydining.data.models.MonthSummary
import com.logicline.mydining.data.models.OtpRequest
import com.logicline.mydining.data.models.Purchase
import com.logicline.mydining.data.models.PurchaseRequest
import com.logicline.mydining.data.models.Report
import com.logicline.mydining.data.models.response.DepositsResponse
import com.logicline.mydining.data.models.response.PurchaseListResponse
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.data.models.Support
import com.logicline.mydining.data.models.User
import com.logicline.mydining.data.models.response.GenericRespose
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.UserGuide
import com.logicline.mydining.data.models.UserMinimalSummary
import com.logicline.mydining.data.models.UserSummary
import com.logicline.mydining.data.models.response.CheckLoginResponse
import com.logicline.mydining.data.models.response.DepositsSumResponse
import com.logicline.mydining.data.models.response.InitialDataResponse
import com.logicline.mydining.data.models.response.MonthlySummaryResponse
import com.logicline.mydining.data.models.response.Paging
import com.logicline.mydining.data.models.response.UserListResponse
import com.logicline.mydining.data.models.response.ProfileResponse
import com.logicline.mydining.data.models.response.ProfileUpdateRequest
import com.logicline.mydining.data.models.response.AvatarUploadResponse
import com.logicline.mydining.data.models.response.*
import com.logicline.mydining.data.requests.MonthCreateRequest
import com.logicline.mydining.utils.AppPrefs
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

    @GET("api/auth/check-login")
    suspend fun checkLogin(
    ): Response<ServerResponse<UserData>>

    @GET("api/summary.getHome.php")
    fun getHomeData(
    ): Call<InitialDataResponse>

    @GET("api/member/initiated/true")
    fun getInitiatedUsers(
        @Header("Month-ID") monthId: Int? = null
    ): Call<ServerResponse<List<MessUser>>>

    @GET("api/user.currentUserInitiated.php")
    fun currentInitiatedUser(
        @Query("date") date: String,
    ): Call<UserListResponse>

    @GET("api/member/list")
    fun getUsers(
        @Query("active") active: Int
    ): Call<ServerResponse<List<MessUser>>>

    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<ServerResponse<UserData>>

    @FormUrlEncoded
    @POST("api/member/create-and-add")
    fun addUser(
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("user_name") userName: String,
        @Field("email") email: String,
        @Field("city") city: String,
        @Field("gender") gender: String,
        @Field("country_id") countryId: String? = null,
        @Field("country_code") countryCode: String? = null,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/auth/sign-up")
    fun signUp(
        @Field("name") name: String,
        @Field("user_name") userName: String,
        @Field("country_code") countryCode: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("email") email: String,
        @Field("city") city: String,
        @Field("gender") gender: String,
    ): Call<ServerResponse<User>>

    @GET("api/meal/list")
    fun getMealByMonth(
        @Header("Month-ID") monthId: Int? = null
    ): Call<ServerResponse<MealsData>>

    @GET("api/summary/months/details")
    fun getMonthSummary(
        @Query("month_id") monthId: Int? = null,
    ): Call<ServerResponse<MonthSummary>>

    @GET("api/meal/user/{messUserId}/by-date")
    fun getUserMealByDate(
        @Path("messUserId") messUserId: Int,
        @Query("date") date: String
    ): Call<ServerResponse<Meal>>

    @GET("api/{type}/list")
    fun getPurchases(
        @Header("Month-ID") monthId: Int? = null,
        @Path("type") type: String
    ): Call<ServerResponse<PurchaseListResponse<Purchase>>>


    @FormUrlEncoded
    @POST("api/meal/add")
    fun addMeal(
        @Field("mess_user_id") messUserId: Int,
        @Field("date") date: String,
        @Field("breakfast") breakfast: Float,
        @Field("lunch") lunch: Float,
        @Field("dinner") dinner: Float,
    ): Call<ServerResponse<Meal>>


    @FormUrlEncoded
    @PUT("api/meal/{mealId}/update")
    fun updateMeal(
        @Path("mealId") mealId: Int,
        @Field("mess_user_id") messUserId: Int,
        @Field("date") date: String? = null,
        @Field("breakfast") breakfast: Float = 0f,
        @Field("lunch") lunch: Float = 0f,
        @Field("dinner") dinner: Float = 0f,
    ): Call<ServerResponse<Meal>>


    @DELETE("api/meal/{mealId}/delete")
    fun deleteMeal(
        @Path("mealId") mealId: Int,
    ): Call<ServerResponse<Void>>

    @FormUrlEncoded
    @POST("api/{type}/add")
    fun addPurchase(
        @Field("mess_user_id") messUserId: Int,
        @Field("date") date: String,
        @Field("product") product: String,
        @Field("price") price: Int,
        @Path("type") type: String,
        @Field("isAddAmount") isAddAmount: Int,
    ): Call<ServerResponse<Purchase>>

    @FormUrlEncoded
    @POST("api/deposit/add")
    fun addDeposit(
        @Field("mess_user_id") messUserId: Int,
        @Field("date") date: String,
        @Field("amount") amount: Float,
    ): Call<ServerResponse<Deposit>>

    @GET("api/deposit/list")
    fun getDeposit(
        @Header("Month-ID") monthId: Int? = null
    ): Call<ServerResponse<DepositsSumResponse>>


    @GET("api/deposit/history/{messUserId}")
    fun getDepositHistory(
        @Header("Month-ID") monthId: Int? = null,
        @Path("messUserId") messUserId: Int
    ): Call<ServerResponse<DepositsResponse>>


    @FormUrlEncoded
    @POST("api/user.changePassword.php")
    fun changePassword(
        @Field("oldPass") oldPass: String,
        @Field("newPass") newPass: String,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/user.changeManager.php")
    fun changeManager(
        @Field("newId") newId: Int,
        @Field("value") value: Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/user.changeSuperUser.php")
    fun changeSuperUser(
        @Field("newId") newId: Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/user.checkUserName.php")
    suspend fun isUserNameAvailable(
        @Field("userName") userName: String,
    ): Response<Boolean>

    @GET("api/mess.isUserInitiate.php")
    suspend fun isUserInitiate(): Response<GenericRespose>

    @GET("api/member/initiated/true")
    suspend fun getInitiatedUser(): Response<ServerResponse<List<MessUser>>>

    @GET("api/member/initiated/false")
    suspend fun getNotInitiatedUser(): Response<ServerResponse<List<MessUser>>>

    @POST("api/member/inititate/add/{messUserId}")
    suspend fun initiateUser(
        @Path("messUserId") messUserId: Int,
    ): Response<ServerResponse<Void>>

    @FormUrlEncoded
    @POST("api/mess.initiateAllUser.php")
    fun initiateAllUser(
        @Field("year") year: String,
        @Field("month") month: String,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/user.updadeFcmToken.php")
    fun updadeFcmToken(
        @Field("token") token: String,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/purchase-request/add")
    fun addPurchaseRequest(
        @Field("product") product: String?,
        @Field("product_json") productJson: String?,
        @Field("date") date: String,
        @Field("price") price: Float,
        @Field("deposit_request") isDepositToAcc: Int,
        @Field("purchase_type") purchaseType: String,
    ): Call<ServerResponse<PurchaseRequest>>

    @FormUrlEncoded
    @POST("api/purchase.requestSinglePurchase.php")
    fun requestSinglePurchase(
        @Field("products") products: String,
        @Field("date") date: String,
        @Field("price") price: Float,
        @Field("isDepositToAcc") isDepositToAcc: Int,
        @Field("purchaseType") purchaseType: String,

        ): Call<GenericRespose>

    @GET("api/purchase-request")
    fun getPurchaseRequests(
        @Query("month-id") monthId: Int? = null,
        @Query("status") status: Int,
    ): Call<ServerResponse<PurchaseListResponse<PurchaseRequest>>>

    @FormUrlEncoded
    @PUT("api/purchase-request/{requestId}/update/status")
    fun acceptPurchaseRequest(
        @Path("requestId") requestId: Int,
        @Field("is_deposit") isDeposit: Int,
        @Field("purchase_type") purchaseType: String? = null,
        @Field("status") status: Int = PurchaseRequestStatus.APPROVED.value,
    ): Call<ServerResponse<Void>>

    @FormUrlEncoded
    @PUT("api/purchase-request/{requestId}/update/status")
    fun rejectPurchaseRequest(
        @Path("requestId") requestId: Int,
        @Field("status") status: Int = PurchaseRequestStatus.REJECTED.value,
    ): Call<ServerResponse<Void>>

    @DELETE("api/purchase-request/{requestId}/delete")
    fun deletePurchaseRequest(
        @Path("requestId") requestId: Int,
    ): Call<ServerResponse<Void>>


    @Multipart
    @POST("api/user.uploadProfileImg.php")
    suspend fun uploadProfileImage(
        @Part pdfFile: MultipartBody.Part,
    ): Response<GenericRespose>


    @GET("api/banner.get.php")
    fun getBanner(
        @Query("name") name: String,
    ): Call<ServerResponse<Banner>>

    @GET("api/deposit/history/{messUserId}")
    fun getDepositByUserIdDate(
        @Path("messUserId") userId: Int,
    ): Call<ServerResponse<DepositHistory>>


    @DELETE("api/deposit/{id}/delete")
    fun deleteDeposit(
        @Path("id") depositId: Int,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @PUT("api/deposit/{id}/update")
    fun updateDeposit(
        @Path("id") depositId: Int,
        @Field("amount") amount: Float,
        @Field("date") date: String,
    ): Call<ServerResponse<Void>>

    @FormUrlEncoded
    @PUT("api/{type}/{id}/update")
    fun updatePurchase(
        @Path("id") id: Int,
        @Field("price") amount: Float,
        @Field("date") date: String,
        @Field("product") products: String,
        @Path("type") type: String,

        ): Call<GenericRespose>


    @DELETE("api/{type}/{id}/delete")
    fun deletePurchase(
        @Path("id") id: Int,
        @Path("type") type: String,
    ): Call<ServerResponse<Void>>

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
        @Field("userId") userId: Int,
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
        @Query("currPage") currPage: Int,
        @Query("totalPage") totalPage: Int
    ): Call<ServerResponse<Paging<UserGuide>>>

    @GET("api/slider.get.php")
    fun getMainSlider(): Call<ServerResponse<MutableList<UserGuide>>>

    @GET("api/settings.getInitialData.php")
    fun getInitialData(
        @Query("version") version: Int,
    ): Call<InitialDataResponse>


    @GET("api/mess.changeAlluserAddMeal.php")
    fun changeAlluserAddMeal(
        @Query("status") status: Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/mess.updateFund.php")
    fun updateFundStatus(
        @Field("fund") fund: Int,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/fund.get.php")
    fun getFunds(
        @Field("year") year: String,
        @Field("month") month: String,
    ): Call<ServerResponse<MutableList<Fund>>>


    @FormUrlEncoded
    @POST("api/fund.add.php")
    fun addFund(
        @Field("date") date: String,
        @Field("comment") comment: String,
        @Field("amount") amount: Float,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/fund.update.php")
    fun updateFund(
        @Field("id") id: Int,
        @Field("date") date: String,
        @Field("comment") comment: String,
        @Field("amount") amount: Float,
    ): Call<GenericRespose>


    @FormUrlEncoded
    @POST("api/fund.delete.php")
    fun deleteFund(
        @Field("id") id: Int,
    ): Call<GenericRespose>


    @GET("api/mess.getAllReport.php")
    fun getAllReport(
        @Query("currPage") currPage: Int,
        @Query("totalPage") totalPage: Int,
    ): Call<ServerResponse<Paging<Report>>>


    @GET("api/report.genereteFull.php")
    fun genereteFullReport(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): Call<ServerResponse<Report>>

    @FormUrlEncoded
    @POST("api/mess.resetByMonth.php")
    fun resetByMonth(
        @Field("year") year: Int,
        @Field("month") month: Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/switchmess.accept.php")
    fun acceptMessMemberJoinRequest(
        @Field("requestId") year: Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/switchmess.cancel.php")
    fun cancelMessMemberJoinRequest(
        @Field("requestId") requestId: Int,
    ): Call<GenericRespose>

    @FormUrlEncoded
    @POST("api/switchmess.request.php")
    fun messSwitchRequest(
        @Field("messId") messId: String,
    ): Call<GenericRespose>

    @GET("api/switchmess.userJoinHistory.php")
    fun userJoinHistory(): Call<ServerResponse<MutableList<MessRequest>>>

    @GET("api/switchmess.messJoinRequest.php")
    fun messJoinRequest(): Call<ServerResponse<MutableList<MessRequest>>>

    @GET("api/mess.getActiveMonthList.php")
    fun getActiveMonthList(): Call<ServerResponse<MutableList<MonthOfYear>>>

    @GET("api/mess.info.php")
    fun getMessInfo(): Call<ServerResponse<Mess>>


    //New Api
    @GET("api/month/list")
    suspend fun getMonths(): Response<ServerResponse<MutableList<Month>>>

    @GET("api/country/list")
    fun getCountries(): Call<ServerResponse<MutableList<Country>>>

    @FormUrlEncoded
    @POST("api/mess/create")
    suspend fun createMess(
        @Field("mess_name") name: String,
    ): Response<ServerResponse<MessUser>>

    @GET("api/mess/mess-user")
    suspend fun messUser(
    ): Response<ServerResponse<MessUser>>

    @GET("api/mess/mess-user/{user}")
    suspend fun messUserById(
        @Path("user") userId: Int? = null,
    ): Response<ServerResponse<MessUser>>

    @POST("api/month/create")
    suspend fun createMonth(
        @Body monthData: MonthCreateRequest
    ): Response<ServerResponse<Month>>

    @GET("api/summary/months/user/minimal")
    suspend fun userMinimalMonthSummary(
        @Query("mess_user_id") messUserId: Int? = null
    ): Response<ServerResponse<UserSummary>>

    @GET("api/summary/months/user/details")
    suspend fun userDetailsMonthSummary(
        @Query("mess_user_id") messUserId: Int? = null
    ): Response<ServerResponse<UserSummary>>

    // Profile Management APIs
    @GET("api/profile")
    suspend fun getProfile(): Response<ServerResponse<ProfileResponse>>

    @PUT("api/profile")
    suspend fun updateProfile(
        @Body profileUpdate: ProfileUpdateRequest
    ): Response<ServerResponse<User>>

    @Multipart
    @POST("api/profile/avatar")
    suspend fun uploadAvatar(
        @Part avatar: MultipartBody.Part
    ): Response<ServerResponse<AvatarUploadResponse>>    @DELETE("api/profile/avatar")
    suspend fun removeAvatar(): Response<ServerResponse<Nothing>>

    // Mess Management APIs
    @GET("api/mess-management/info")
    suspend fun getCurrentMessInfo(): Response<ServerResponse<MessInfoResponse>>

    @POST("api/mess-management/leave")
    suspend fun leaveMess(): Response<ServerResponse<Nothing>>

    @FormUrlEncoded
    @POST("api/mess-management/close")
    suspend fun closeMess(
        @Field("confirmation") confirmation: Boolean = true,
        @Field("reason") reason: String? = null
    ): Response<ServerResponse<Nothing>>

    @GET("api/mess-management/available")
    suspend fun getAvailableMesses(
        @Query("search") search: String? = null,
        @Query("limit") limit: Int? = null
    ): Response<ServerResponse<List<AvailableMessesResponse>>>

    @FormUrlEncoded
    @POST("api/mess-management/join-request/{mess}")
    suspend fun sendJoinRequest(
        @Path("mess") messId: Int,
        @Field("message") message: String? = null
    ): Response<ServerResponse<JoinRequestResponse>>

    @GET("api/mess-management/join-requests")
    suspend fun getUserJoinRequests(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int? = null
    ): Response<ServerResponse<UserJoinRequestsResponse>>

    @DELETE("api/mess-management/join-requests/{request}")
    suspend fun cancelJoinRequest(
        @Path("request") requestId: Int
    ): Response<ServerResponse<Nothing>>

    @GET("api/mess-management/incoming-requests")
    suspend fun getMessJoinRequests(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int? = null
    ): Response<ServerResponse<IncomingJoinRequestsResponse>>

    @FormUrlEncoded
    @POST("api/mess-management/incoming-requests/{request}/accept")
    suspend fun acceptJoinRequest(
        @Path("request") requestId: Int,
        @Field("welcome_message") welcomeMessage: String? = null,
        @Field("assign_role") assignRole: String? = null,
        @Field("initiate_for_current_month") initiateForCurrentMonth: Boolean? = null
    ): Response<ServerResponse<AcceptJoinRequestResponse>>

    @FormUrlEncoded
    @POST("api/mess-management/incoming-requests/{request}/reject")
    suspend fun rejectJoinRequest(
        @Path("request") requestId: Int,
        @Field("reason") reason: String? = null,
        @Field("allow_future_requests") allowFutureRequests: Boolean? = null
    ): Response<ServerResponse<RejectJoinRequestResponse>>

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
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Host", "md.local") // Add custom Host header
                        .build()
                    chain.proceed(request)
                }
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
                    } catch (e: Exception) {
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

    class TokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val token = if (AppPrefs.accessToken != null) AppPrefs.accessToken else ""
            val userId = if (AppPrefs.userId != null) AppPrefs.userId else ""
            val monthId = if (AppPrefs.monthId != null) AppPrefs.monthId else ""


            return if (!token.isNullOrEmpty()) {
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .header("Authorization", "Bearer $token")
                        .header("Userid", "$userId")
                        .header("Month-ID", "$monthId")
                        .build()
                )
            } else {
                chain.proceed(chain.request())
            }
        }

    }


}