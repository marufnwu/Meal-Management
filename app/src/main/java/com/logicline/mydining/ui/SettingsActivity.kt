package com.logicline.mydining.ui
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.view.View
import com.dibyendu.picker.listener.PickerListener
import com.dibyendu.picker.util.PickerUtils
import com.dibyendu.picker.view.MonthYearPickerDialog
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivitySettingsBinding
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.DialogMaker
import com.logicline.mydining.utils.LanguageSelectorDialog
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.maruf.jdialog.JDialog
import com.whiteelephant.monthpicker.MonthPickerDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

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
        supportActionBar?.title = getString(R.string.settings)

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

        binding.cardSwitchMess.setOnClickListener {
            startActivity(Intent(this, SwitchMessActivity::class.java))
       }

        binding.cardResetMess.setOnClickListener {
            if(Constant.isSuperUser()){
                showResetDialog()
            }else{
                showOtherUserDialog()
            }
        }
        binding.cardChangeLanguage.setOnClickListener {
            LanguageSelectorDialog.Builder(this)
                .build()
                .show()
        }

        binding.cardResetMonth.setOnClickListener {

            showDateSelectionDialog()
            JDialog.make(this)
                .setCancelable(true)
                .setIconType(JDialog.IconType.WARNING)
                .setBodyText(getString(R.string.reset_month_warning))
        }

        if(Constant.isManagerOrSuperUser()){
            binding.cardMemberRequest.visibility  =View.VISIBLE
            binding.cardMemberRequest.setOnClickListener {
                startActivity(Intent(this, MemberRequestActivity::class.java))
            }
        }

        binding.cardMessInfo.setOnClickListener {
            startActivity(Intent(this, MessInfoActivity::class.java))
        }

        binding.cardMessType.setOnClickListener {
//            DialogMaker.SelectMessTypeDialog(this, LocalDB.getInitialData()?.messData?.type!!, onSubmit = { dialog, type ->
//
//            })

            startActivity(Intent(this, MessTypeActivity::class.java))
        }

    }

    private fun showDateSelectionDialog() {
        val  builder = MonthPickerDialog.Builder(this, { m, y ->
            showWarningDialog( y, m+1)


        },Constant.getCurrentYear().toInt(), Constant.getCurrentMonthNumber().toInt()-1)

        builder.setTitle("Select Month")
            .build()
            .show()
    }

    private fun showWarningDialog(year:Int, month:Int) {
        JDialog.make(this)
            .setCancelable(true)
            .setIconType(JDialog.IconType.WARNING)
            .setBodyText(getString(R.string.reset_month_warning).format(Constant.getMonthName("$year-$month-01")+" $year"))
            .setPositiveButton(getString(R.string.delete)){
                it.hideDialog()
                processMonthDelete(year, month)

            }.setNegativeButton(getString(R.string.cancel)){
                it.hideDialog()
            }
            .build()
            .showDialog()
    }

    private fun processMonthDelete(year: Int, month: Int) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .resetByMonth(year, month)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        shortToast(message = response.body()!!.msg)
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
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