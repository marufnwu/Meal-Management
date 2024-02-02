package com.logicline.mydining.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.logicline.mydining.R
import com.logicline.mydining.databinding.DialogStartNewMonthLayoutBinding
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.InitialDataResponse
import com.logicline.mydining.network.MyApi
import com.logicline.mydining.ui.InitiateMemberActivity
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseActivity(private val checkUserInitiate: Boolean = false) : AppCompatActivity() {
    protected var month : String? = Constant.getCurrentMonthNumber()
    protected var year : String? = Constant.getCurrentYear()
    protected var force : Boolean = false
    protected var monthId : Int? = null
    protected var monthName : String? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        MyApi.isForceCheck = force
        MyApi.checkType = MyApi.Companion.CHECK_TYPE.MANUAL

        intent?.let {
            it.getStringExtra(Constant.YEAR)?.let {
                year = it
            }

            it.getStringExtra(Constant.MONTH)?.let {
                month = it
            }



            it.getIntExtra(Constant.MONTH_ID, 0).let {
                monthId = if(it==0) null else it

            }

            it.getBooleanExtra(Constant.FORCE, false).let {
                force =  it
                MyApi.isForceCheck = it
                Toast.makeText(this, force.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase!=null) {
            super.attachBaseContext(LangUtils.applyLanguage(newBase))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onStart() {
        super.onStart()

        //check mess type manual or not

        if(Constant.isManagerOrSuperUser() ){

            if(Constant.getMessType() == Constant.MessType.MANUALLY && Constant.getMonthId()==null){
                //show create new month dialog
                showCreateNewMonthIdDialog();
                return
            }


            if( checkUserInitiate){
                checkIsMemberInitiate()
            }
        }
    }

    private fun showCreateNewMonthIdDialog() {
        com.maruf.jdialog.JDialog.make(this)
            .setCancelable(false)
            .setPositiveButton("Start Now"){
                it.hideDialog()
                showCreateNewMonthDialog()
            }
            .setNegativeButton("Dismiss"){
                it.hideDialog()
            }
            .setBodyText("Your mess type is Manual. And you didn't start a month. So start a new month first.")
            .setIconType(com.maruf.jdialog.JDialog.IconType.WARNING)
            .build()
            .showDialog()
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
        val loadingDialog = LoadingDialog(this)
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

//                    setInitialData()
                    dismissCallback.invoke()
                    startActivity(intent)
                }

                override fun onFailure(call: Call<InitialDataResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun checkIsMemberInitiate() {
        Coroutines.main {
            val res = (application as MyApplication)
                .myApi
                .isUserInitiate()

            if (res.isSuccessful && res.body()!=null){
                if(!res.body()!!.error){
                    //user already initiated of this month
                }else{
                    //user not initiated of this month
                    //show the dialog
                    JDialog.make(this@BaseActivity)
                        .setCancelable(true)
                        .setBodyText(res.body()?.msg)
                        .setIconType(JDialog.IconType.WARNING)
                        .setShowNegativeButton(true)
                        .setShowPositiveButton(true)
                        .setPositiveButtonText(getString(R.string.start_new_month))
                        .setNegativeButtonText(getString(R.string.cancel))
                        .setOnGenericDialogListener(object : JDialog.OnGenericDialogListener {
                            override fun onPositiveButtonClick(dialog: JDialog?) {
                                startCurrentMonth(dialog)
                            }

                            override fun onNegativeButtonClick(dialog: JDialog?) {
                                dialog?.hideDialog()
                            }

                            override fun onToast(message: String?) {
                            }

                        }).build().showDialog()
                }
            }
        }
    }

    private fun startCurrentMonth(dialog:JDialog?){
        var progressDialog: LoadingDialog = LoadingDialog(this)

        progressDialog.show()
        (application as MyApplication)
            .myApi
            .initiateAllUser(Constant.getCurrentYear(), Constant.getCurrentMonthNumber())
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    progressDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            dialog?.hideDialog()
                            finish()
                            startActivity(intent)
                        }

                        shortToast(response.body()!!.msg)
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    progressDialog.hide()
                }

            })
    }
}