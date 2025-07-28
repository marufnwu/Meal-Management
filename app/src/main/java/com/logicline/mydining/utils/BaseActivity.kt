package com.logicline.mydining.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.logicline.mydining.R
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.ui.InitiateMemberActivity
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseActivity(private val checkUserInitiate: Boolean = false) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
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
        if(Constant.isManagerOrSuperUser() && checkUserInitiate){
            checkIsMemberInitiate()
        }
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