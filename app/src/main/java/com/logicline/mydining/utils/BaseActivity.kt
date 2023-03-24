package com.logicline.mydining.utils

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
import com.logicline.mydining.ui.InitiateMemberActivity

open class BaseActivity(private val checkUserInitiate: Boolean = true) : AppCompatActivity() {

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
                        .setPositiveButtonText("Add Now")
                        .setOnGenericDialogListener(object : JDialog.OnGenericDialogListener {
                            override fun onPositiveButtonClick(dialog: JDialog?) {
                                dialog?.hideDialog()
                                startActivity(Intent(this@BaseActivity, InitiateMemberActivity::class.java))
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
}