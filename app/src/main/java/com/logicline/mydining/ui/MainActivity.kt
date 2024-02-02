package com.logicline.mydining.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.adapter.MainSliderAdapter
import com.logicline.mydining.databinding.ActivityMainBinding
import com.logicline.mydining.databinding.DialogStartNewMonthLayoutBinding
import com.logicline.mydining.models.Banner
import com.logicline.mydining.models.Support
import com.logicline.mydining.models.User
import com.logicline.mydining.models.UserGuide
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.InitialDataResponse
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.*
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.maruf.jdialog.JDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"

    companion object {
        const val HOME_MAIN_BANNER: String = "homeMain"
    }

    lateinit var binding : ActivityMainBinding
    var user : User? = null
    lateinit var loadingDialog: LoadingDialog
    lateinit var mainBottomSheet: MainBottomSheet

    var userGuideBanner : MutableLiveData<Banner> = MutableLiveData()

    private var rotationAngle = 0f
    private var isUserGuideExpand = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(this)
        setContentView(binding.root)
        mainBottomSheet = MainBottomSheet.newInstance()


        user = LocalDB.getUser()

        if(user==null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        user?.let {
            it.phone?.let {
                binding.txtUserPhone.text = it
            }

            it.name?.let {
                binding.txtUserName.text = it
            }

            it.photoUrl?.let {
                Glide.with(this)
                    .load(BuildConfig.BASE_URL+it)
                    .into(binding.imgProPic)
            }



        }


        val month = Constant.getCurrentMonthName()+" "+Constant.getCurrentYear()
        if(Constant.getMessType()==Constant.MessType.MANUALLY){
            binding.txtCurrentMonth.text = Constant.getCurrentMonth()?.name

        }else{
            binding.txtCurrentMonth.text = month
        }

        if(!Constant.isManagerOrSuperUser()){


            user?.let {
                if(it.allUserAddMeal==0){
                    //regular user can't add meal
                    binding.addMeal.visibility = View.GONE
                }
            }

            binding.initiateMember.visibility = View.GONE
            binding.addPurchase.visibility = View.GONE



        }
        askNotificationPermission()
        initListener()

        //getInitialData()
        setInitialData();
        getHomeMainBanner()




        registerFcm()
        updateFcmToken()

        getSliderData()

    }

    private fun showCreateNewNotice() {
        if(Constant.isManagerOrSuperUser()){
            JDialog.make(this)
                .setCancelable(false)
                .setPositiveButton("Start Now"){
                    it.hideDialog()
                    showCreateNewMonthDialog()
                }
                .setNegativeButton("Dismiss"){
                    it.hideDialog()
                }
                .setBodyText("Your mess type Manual. And you didn't start a month. So start a new month first.")
                .setIconType(JDialog.IconType.WARNING)
                .build()
                .showDialog()
        }

    }

    private fun setInitialData() {
        val initialData = LocalDB.getInitialData()
        initialData?.let {
            binding.txtMealcharge.text = it.mealCharge
            binding.txtTotalMeal.text = it.totalMeal

            it.messName?.let {
                binding.txtCurrentMonth.text  =it
            }

            it.messData?.let {mess->
                when(mess.type){
                    1 -> Log.i(TAG, "setInitialData: mess is auto type")
                    2 -> if(it.month==null) {
                        showCreateNewNotice()
                    } else{
                        //do nothing
                    }
                    else -> showSetMessTypeDialogNotice()
                }
            }
        }
    }

    private fun showCreateNewMonthDialog() {
        val dialogBinding = DialogStartNewMonthLayoutBinding.inflate(layoutInflater);

        val builder= AlertDialog.Builder(this)
            .setCancelable(true)
            .setView(dialogBinding.root)

        val dialog=builder.create()
        dialogBinding.btnSubmit.setOnClickListener {
            val name = dialogBinding.edtName.text.toString()
            if(name.isNullOrEmpty()){
                Toast.makeText(this, "Please enter name of month to start", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startNewMonth(name, object : () -> Unit {
                override fun invoke() {
                    dialog.dismiss()
                }

            })
        }
        dialog.show()



    }

    private fun startNewMonth(name: String, dismissCallback : () ->Unit) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .startNewMonth(name)
            .enqueue(object : Callback<InitialDataResponse> {
                override fun onResponse(
                    call: Call<InitialDataResponse>,
                    response: Response<InitialDataResponse>
                ) {
                    loadingDialog.hide()
                    response.body()?.let {
                        it.initialData?.let {
                            LocalDB.saveInitialData(it)
                        }
                    }

                    setInitialData()
                    dismissCallback.invoke()
                }

                override fun onFailure(call: Call<InitialDataResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun showSetMessTypeDialogNotice() {

        if(Constant.isManagerOrSuperUser()){
            JDialog.make(this)
                .setCancelable(false)
                .setPositiveButton("Select Now"){
                    showSetMessTypeDialog()
                    it.hideDialog()
                }
                .setNegativeButton("Dismiss"){
                    it.hideDialog()
                }
                .setBodyText("You didn't specify your mess type. Please select your mess type.")
                .setIconType(JDialog.IconType.WARNING)
                .build()
                .showDialog()
        }

    }

    private fun showSetMessTypeDialog() {
        var messType = 0
        LocalDB.getInitialData()?.messData?.type?.let {
             messType = it
        }
        DialogMaker.SelectMessTypeDialog(this, messType ){ dialog, type ->
            loadingDialog.show()
            (application as MyApplication)
                .myApi
                .setMessType(type)
                .enqueue(object : Callback<GenericRespose> {
                    override fun onResponse(
                        call: Call<GenericRespose>,
                        response: Response<GenericRespose>
                    ) {
                       loadingDialog.hide()
                        response.body()?.let {
                            Toast.makeText(this@MainActivity, it.msg, Toast.LENGTH_SHORT).show()
                            if (it.error){
                                return
                            }

                            dialog.dismiss()
                            freshReload()
                        }
                    }

                    override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                        loadingDialog.hide()
                        Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                    }

                })
        }.show()
    }

    private fun getSliderData() {
        (application as MyApplication)
            .myApi
            .getMainSlider()
            .enqueue(object : Callback<ServerResponse<MutableList<UserGuide>>> {
                override fun onResponse(
                    call: Call<ServerResponse<MutableList<UserGuide>>>,
                    response: Response<ServerResponse<MutableList<UserGuide>>>
                ) {
                    response.body()?.let {
                        if (!it.error){
                            it.data?.let {
                                if(it.size>0){
                                    binding.slider.visibility = View.VISIBLE
                                    val sliderAdapter = MainSliderAdapter(this@MainActivity, it)
                                    binding.slider.setSliderAdapter(sliderAdapter)
                                    binding.slider.isAutoCycle = true
                                }
                            }
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<MutableList<UserGuide>>>,
                    t: Throwable
                ) {

                }

            })
    }

    private fun getHomeMainBanner(){
        (application as MyApplication)
            .myApi
            .getBanner(HOME_MAIN_BANNER)
            .enqueue(object : Callback<ServerResponse<Banner>> {
                override fun onResponse(call: Call<ServerResponse<Banner>>, res: Response<ServerResponse<Banner>>) {
                    if(res.isSuccessful && res.body()!=null){

                        if(!res.body()!!.error){
                            val bannerRes = res.body()!!.data!!
                            userGuideBanner.postValue(bannerRes)
                        }

                    }
                }

                override fun onFailure(call: Call<ServerResponse<Banner>>, t: Throwable) {

                }

            })

    }

    private fun updateFcmToken() {
        user?.let {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {task->
                if(task.isSuccessful){
                    task.result?.let {
                        (application as MyApplication).myApi
                            .updadeFcmToken(it)
                            .enqueue(object : Callback<GenericRespose?> {
                                override fun onResponse(
                                    call: Call<GenericRespose?>,
                                    response: Response<GenericRespose?>
                                ) {
                                }

                                override fun onFailure(call: Call<GenericRespose?>, t: Throwable) {}
                            })
                    }
                }

            }
        }
    }

    private fun registerFcm() {
        user?.let {
            Firebase.messaging.subscribeToTopic("mess_${it.messId}")
                .addOnCompleteListener {}

            Firebase.messaging.subscribeToTopic("allUser")
                .addOnCompleteListener {}
        }
    }

    private fun getInitialData() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getInitialData(BuildConfig.VERSION_CODE)
            .enqueue(object : Callback<InitialDataResponse> {
                override fun onResponse(
                    call: Call<InitialDataResponse>,
                    response: Response<InitialDataResponse>) {
                    loadingDialog.hide()
                    if(response.isSuccessful){
                        response.body()?.let { initialDataResponse ->
                            if(!initialDataResponse.error){
                                initialDataResponse.initialData?.let {
                                    binding.txtMealcharge.text = it.mealCharge
                                    binding.txtTotalMeal.text = it.totalMeal

                                    LocalDB.saveInitialData(it)
                                    setCustomerSupport(it.support)

                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<InitialDataResponse>, t: Throwable) {
                    shortToast(t.message)
                    loadingDialog.hide()

                    JDialog.make(this@MainActivity)
                        .setCancelable(false)
                        .setBodyText("Something went wrong! Please try again.")
                        .setIconType(JDialog.IconType.ERROR)
                        .setPositiveButton("Try Again"){
                            it.hideDialog()
                            getInitialData()
                        }.build()
                        .showDialog()
                }

            })
    }

    private fun setCustomerSupport(support: Support?) {

        support?.let {

            if(it.active){
                binding.imgCust.visibility = View.VISIBLE
                binding.cardContactUs.visibility = View.VISIBLE

                binding.cardContactUs.setOnClickListener {
                    if(support.type=="whatsapp"){
                        Constant.openWpCustomerCare(this, support.action)
                    }else if(support.type=="link"){
                        Constant.openLink(this, support.action)
                    }
                }

                binding.imgCust.setOnClickListener {
                    if(support.type=="whatsapp"){
                        Constant.openWpCustomerCare(this, support.action)
                    }else if(support.type=="link"){
                        Constant.openLink(this, support.action)
                    }
                }
            }
        }

    }

    private fun reload(){
        startActivity(intent)
        finish()
    }


    private fun freshReload(){
        startActivity(Intent(this, FirstActivity::class.java))
        finish()
    }

    private fun initListener() {


        binding.layoutRefresh.setOnRefreshListener {
            reload()
        }

        userGuideBanner.observe(this){
            if(it!=null){
                Constant.setBanner(binding.imgUserGuide, it, this)

                if (LocalDB.isFirstOpen()){
                    toggleUserGuide()
                    LocalDB.setFirstOpen(false)
                }
            }
        }

        binding.addFund.setOnClickListener {
            startActivity(Intent(this, FundActivity::class.java))
        }


        binding.members.setOnClickListener {
            startActivity(Intent(this, MembersActivity::class.java))
        }
        binding.imgSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }


        binding.mealChart.setOnClickListener {
            startActivity(Intent(this, MealActivity::class.java))
        }

        binding.purchases.setOnClickListener {
            startActivity(Intent(this, PurchasesActivity::class.java).putExtra(Constant.PURCHASE_TYPE, 1))
        }

        binding.otherCost.setOnClickListener {
            startActivity(Intent(this, PurchasesActivity::class.java).putExtra(Constant.PURCHASE_TYPE, 2))
        }

        binding.addMeal.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }


        binding.deposit.setOnClickListener {
            startActivity(Intent(this, DepositActivity::class.java))
        }

        binding.summary.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        binding.imgProPic.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).putExtra("profile",user))

        }

        binding.initiateMember.setOnClickListener {
            startActivity(Intent(this, InitiateMemberActivity::class.java))

        }

        binding.addPurchase.setOnClickListener {
            startActivity(Intent(this, AddPurchaseActivity::class.java))
        }
        binding.purchaseRequest.setOnClickListener {
            startActivity(Intent(this, PurchaseRequestActivity::class.java))
        }

        binding.oldData.setOnClickListener {
            startActivity(Intent(this, ActiveMonthActivity::class.java))
        }


        binding.imgDrawer.setOnClickListener {
            if(supportFragmentManager.findFragmentByTag("MainBottomSheet")==null){
                mainBottomSheet.show(supportFragmentManager, "MainBottomSheet")
            }
        }






        binding.layoutFaq.setOnClickListener {
            Constant.openWebView("Frequently ask question", BuildConfig.BASE_URL+"faq.html", this)

        }

        binding.layoutUserGideToggle.setOnClickListener {
            toggleUserGuide()
        }
    }

    private fun toggleUserGuide() {
        rotationAngle = if (rotationAngle == 0f) 180f else 0f //toggle
        binding.imgDownExpand.animate().rotation(rotationAngle).setDuration(500).start()

        if(isUserGuideExpand){
            Constant.collapse(binding.layoutUserGuide)
            //binding.layoutUserGuide.visibility  = View.GONE
        }else{
            Constant.expand(binding.layoutUserGuide)

            //binding.layoutUserGuide.visibility  = View.VISIBLE
        }

        isUserGuideExpand = !isUserGuideExpand
    }

    private fun askNotificationPermission(){
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.


            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {

            }
            else -> {
                // The registered ActivityResultCallback gets the result of this request
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    override fun onStart() {
        super.onStart()


        if(!MyApplication.isLogged()){
            startActivity(Intent(this, FirstActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }

        if(LocalDB.getInitialData()?.messData?.fundStatus==0){
            binding.addFund.visibility = View.GONE
        }else{
            binding.addFund.visibility = View.VISIBLE
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase!=null) {
            super.attachBaseContext(LangUtils.applyLanguage(newBase))
        } else {
            super.attachBaseContext(newBase)
        }
    }



}