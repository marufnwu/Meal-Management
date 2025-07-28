package com.logicline.mydining.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import com.bumptech.glide.Glide
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.R
import com.logicline.mydining.models.Banner
import com.logicline.mydining.models.UserType
import com.logicline.mydining.network.MyApi
import com.logicline.mydining.ui.GenericWebViewActivity
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


object Constant {

    val APP_LANG_KEY: String = "APPLANGUAGE"
    const val MONTH: String = "Month"
    const val YEAR: String = "Year"
    const val HISTORY_TYPE: String = "HISTORY_TYPE"
    const val MESS_ID = "MESS_ID"
    const val USER_ID = "USER_ID"
    const val WEB_URL: String = "WEB_URL"
    const val ACTIVITY_NAME: String ="ACTIVITY-NAME"
    const val PRIVACY_URL = "privacy-policy.html"
    const val TERMS_CONDITION_URL = "terms-and-condition.html"
    const val CONTACT_US_URL = "contact-us.html"

    const val ACTIVITY: String = "NotificationActivity"
    const val ACTIVITY_CREATED_BY_NOTI: String = "ACTIVITY_CREATED_BY_NOTI"

    val PURCHASE_TYPE: String = "PURCHASE_TYPE"
    val MESS_MANAGER = "2"
    val NORMAL_USER = "0"
    val SUPER_USER = "3"

    var userNameCheckListener: ((Boolean) -> Boolean?)= { false }

    val userKey = "User"

     enum class VIEW_TYPE{
        NORMAL_ITEM,
        NATIVE_AD_ITEM
    }

    enum class LANGUAGE{
        en_US,
        bn
    }

    fun isManager():Boolean{
        val user = LocalDB.getUser()
        user?.let {
            if(it.accType=="2"){
                return true
            }
        }
        return false
    }
    fun isSuperUser():Boolean{
        val user = LocalDB.getUser()
        user?.let {
            if(it.accType=="3"){
                return true
            }
        }
        return false
    }

    fun isManagerOrSuperUser():Boolean{
        val user = LocalDB.getUser()
        user?.let {
            if(it.accType=="3" || it.accType=="2"){
                return true
            }
        }
        return false
    }


    @SuppressLint("SimpleDateFormat")
    fun getDayNameFromDate(date:String) : String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale("en"))
        val dayName: String = simpleDateFormat.format(myDate)

        return  dayName
    }

    @SuppressLint("SimpleDateFormat")
    fun dateFormat(date:String) : String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))
        val dayName: String = simpleDateFormat.format(myDate)

        return  dayName
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate() : String{
        val cal = Calendar.getInstance()
        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))
        return inFormat.format(Date())
    }
    @SuppressLint("SimpleDateFormat")
    fun getTomorrowDate() : String{
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE ,1)
        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))
        return inFormat.format(cal.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthName(date:String):String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd",Locale("en"))

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("MMMM", Locale("en"))

        val c = Calendar.getInstance()
        c.time = myDate

        val dayName: String = simpleDateFormat.format(myDate)

        return  dayName
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthNumber(date:String):String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd",Locale("en"))

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("MM", Locale("en"))
        val month: Int = simpleDateFormat.format(myDate).toInt()

        return  month.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getYear(date:String):String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd",Locale("en"))

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("yyyy",Locale("en"))
        val dayName: String = simpleDateFormat.format(myDate)

        return dayName
    }

    @SuppressLint("SimpleDateFormat")
    fun getDay(date:String):String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd",Locale("en"))

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("dd",Locale("en"))
        val dayName: String = simpleDateFormat.format(myDate)

        return dayName
    }

    fun getCurrentYear():String{
        return  getYear(getCurrentDate())
    }
    fun getCurrentDay():String{
        return  getDay(getCurrentDate())
    }

    fun getCurrentMonthName():String{
        return  getMonthName(getCurrentDate())
    }

    fun getCurrentMonthNumber():String{
        return getMonthNumber(getCurrentDate())
    }

    fun getCurrentDayName():String{
        return getDayNameFromDate(getCurrentDate())
    }

    fun booleanToInt(value:Boolean):Int{
        return if(value){
            1
        }else{
            0
        }
    }

    suspend fun checkUserName(api : MyApi, userName:String, myCallback: (result: Boolean?) -> Unit){
        val res = api.isUserNameAvailable(userName)
            .body()

       myCallback.invoke(res)

    }

    fun openPrivacyPolicy(context: Context){
        val intent = Intent(context, GenericWebViewActivity::class.java)
        intent.putExtra(ACTIVITY_NAME, "Privacy Policy")
        intent.putExtra(WEB_URL, BuildConfig.BASE_URL+ PRIVACY_URL)

        context.startActivity(intent)
    }


    fun openWebView(pageTitle:String, url:String, context: Context){
        val intent = Intent(context, GenericWebViewActivity::class.java)
        intent.putExtra(ACTIVITY_NAME, pageTitle)
        intent.putExtra(WEB_URL, url)

        context.startActivity(intent)
    }
    fun openTermsAndCondition(context: Context){
        val intent = Intent(context, GenericWebViewActivity::class.java)
        intent.putExtra(ACTIVITY_NAME, "Terms and Conditions")
        intent.putExtra(WEB_URL, BuildConfig.BASE_URL+ TERMS_CONDITION_URL)

        context.startActivity(intent)
    }
    fun openContactUs(context: Context){
        val intent = Intent(context, GenericWebViewActivity::class.java)
        intent.putExtra(ACTIVITY_NAME, "Contact Us")
        intent.putExtra(WEB_URL, BuildConfig.BASE_URL+ CONTACT_US_URL)

        context.startActivity(intent)
    }

    fun getUserType(type:String?) : String{

        if(type==null){
            return "Undefined"
        }

        return when (type.toInt()){
            UserType.NORMAL_USER -> "User"
            UserType.MANAGER -> "Manager"
            UserType.SUPER_USER -> "Super Admin"
            else -> "Undefined"
        }
    }


    fun setBanner(imageView: ImageView, banner: Banner?, context: Context){
        banner?.let {
            if(it.visible==1){
                imageView.visibility  =View.VISIBLE
                Glide.with(context)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.loading)
                    .into(imageView)

                if (it.actionType==1){
                    it.actionUrl?.let {link->
                        imageView.setOnClickListener {
                            openLink(context, link)
                        }
                    }
                }
            }else{
                imageView.visibility = View.GONE
            }
        }

    }


    fun openLink(context: Context, url:String?){


        if(url==null){
            return
        }


        val linkHost = Uri.parse(url).host
        val uri = Uri.parse(url)
        if (linkHost == null) {
            return
        }
        if (linkHost == "play.google.com") {
            val appId = uri.getQueryParameter("id")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$appId")
            context.startActivity(intent)
        } else if (linkHost == "www.youtube.com") {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.google.android.youtube")
                context.startActivity(intent)
            }catch (e : Exception){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
        else if (url != null && (url.startsWith("http://") || url.startsWith(
                "https://"
            ))
        ) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun openWpCustomerCare(context: Context, wpNumber:String){
        try {
            val i = Intent(Intent.ACTION_VIEW)

            if(MyApplication.isLogged()){
                i.data = Uri.parse(
                    "whatsapp://send?phone=${wpNumber}&text=" + URLEncoder.encode(
                        "Name: ${LocalDB.getUser()?.name}\n" +
                                "Phone: ${LocalDB.getUser()?.phone}\n" +
                                "Username: ${LocalDB.getUser()?.userName}\n" +
                                "\nType Your message here ...\n\n",
                        "UTF-8"
                    )
                )
            }else{
                i.data = Uri.parse(
                    "whatsapp://send?phone=${wpNumber}&text=" + URLEncoder.encode(
                        "Type Your message here ...\n\n",
                        "UTF-8"
                    )
                )
            }


            context.startActivity(i)
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Please install Whatsapp !", Toast.LENGTH_LONG).show()
        }
    }

    fun openCustomerCare(context: Context){
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(
                "whatsapp://send?phone=" + "+8801643145113" + "&text=" + URLEncoder.encode(
                    "Message\n",
                    "UTF-8"
                )
            )
            context.startActivity(i)
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Whatsapp not installed!", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getAccAgeInDays():Int{
        val user = LocalDB.getUser()
        user?.let {
            it.joinDate?.let {
                val df = SimpleDateFormat("yyyy-MM-dd")
                val jDate = df.parse(it)
                val cDate = df.parse(getCurrentDate())

                if (cDate != null && jDate != null) {
                    return TimeUnit.MILLISECONDS.toDays(cDate.time - jDate.time).toInt()
                }

            }
        }

        return 0
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    fun getActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) return context
        return if (context is ContextWrapper) getActivity(context.baseContext) else null
    }

     fun checkWriteStoragePermission(context: Context):Boolean {
        return if(Build.VERSION.SDK_INT< Build.VERSION_CODES.R){
            context.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
        }else{
            true
        }
    }


}