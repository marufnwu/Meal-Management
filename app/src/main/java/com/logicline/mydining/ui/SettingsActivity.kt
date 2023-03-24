package com.logicline.mydining.ui
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivitySettingsBinding
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.maruf.jdialog.JDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : BaseActivity(false) {
    lateinit var binding: ActivitySettingsBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog  = LoadingDialog(this)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        initViews()


    }

    private fun initViews() {


        LocalDB.getInitialData()?.let {
            it.messData?.let {
                binding.switchMealAdd.isChecked = it.allUserAddMeal==1
                binding.switchFundAdd.isChecked = it.fundStatus!=0
            }
        }

        binding.switchMealAdd.isEnabled = Constant.isManagerOrSuperUser()
        binding.switchFundAdd.isEnabled = Constant.isManagerOrSuperUser()

        binding.switchMealAdd.setOnCheckedChangeListener { buttonView, isChecked ->
            changeAllUserMealAdd(isChecked)
        }

        binding.switchFundAdd.setOnCheckedChangeListener { buttonView, isChecked ->
            changeFundAdd(isChecked)
        }


        binding.cardChangeSuperUser.setOnClickListener {
            startActivity(Intent(this, ChangeSuperuserActivity::class.java))
        }

       binding.cardUserGuide.setOnClickListener {
            startActivity(Intent(this, UserGuideActivity::class.java))
       }

        binding.cardGenReport.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
       }

        binding.cardResetMess.setOnClickListener {
            if(Constant.isSuperUser()){
                showResetDialog()
            }else{
                showOtherUserDialog()
            }
        }
    }

    private fun changeFundAdd(checked: Boolean) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .updateFundStatus(Constant.booleanToInt(checked))
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)
                        if(!response.body()!!.error){
                            val changeValue = LocalDB.getInitialData()!!
                            changeValue.messData!!.fundStatus = Constant.booleanToInt(checked)
                            LocalDB.saveInitialData(changeValue)
                        }else{
                            binding.switchFundAdd.isChecked = !checked
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    binding.switchFundAdd.isChecked = !checked
                    loadingDialog.hide()
                }

            })
    }

    private fun changeAllUserMealAdd(checked: Boolean) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .changeAlluserAddMeal(Constant.booleanToInt(checked))
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)
                        if(!response.body()!!.error){
                            val changeValue = LocalDB.getInitialData()!!
                            changeValue.messData!!.allUserAddMeal = Constant.booleanToInt(checked)
                            LocalDB.saveInitialData(changeValue)
                        }else{
                            binding.switchMealAdd.isChecked = !checked
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    binding.switchMealAdd.isChecked = !checked
                    loadingDialog.hide()
                }

            })
    }

    private fun showResetDialog() {

        JDialog.make(this)
            .setCancelable(true)
            .setIconType(JDialog.IconType.WARNING)
            .setBodyText("if you reset mess, All of data and members will be removed. Are want to reset the mess?")
            .setNegativeButton("No"){
                it.hideDialog()
            }
            .setPositiveButton("Yes"){
                it.hideDialog()
                resetMess()
            }.build()
            .showDialog()
    }

    private fun resetMess() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .resetMess(Constant.getCurrentYear(), Constant.getCurrentMonthNumber())
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })
    }

    private fun showOtherUserDialog() {

        JDialog.make(this)
            .setCancelable(true)
            .setIconType(JDialog.IconType.ERROR)
            .setBodyText("Only super can reset mess. You are not super user.")
            .setPositiveButton("Cancel"){
                it.hideDialog()
            }.build()
            .showDialog()
    }
}