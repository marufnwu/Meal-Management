package com.maruf.messmanagement.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.maruf.messmanagement.R
import com.maruf.messmanagement.databinding.ActivityProfileBinding
import com.maruf.messmanagement.databinding.DialogAddNewMemberBinding
import com.maruf.messmanagement.databinding.DialogPasswordChangeBinding
import com.maruf.messmanagement.models.User
import com.maruf.messmanagement.models.response.GenericRespose
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.LoadingDialog
import com.maruf.messmanagement.utils.MyApplication
import com.maruf.messmanagement.utils.MyExtensions.shortToast
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityProfileBinding
    lateinit var loadingDialog: LoadingDialog

    var userProfile : User? = null
    var loggedUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userProfile = intent.getParcelableExtra<User>("profile")
        loggedUser = Paper.book().read(Constant.userKey)

        loadingDialog = LoadingDialog(this)


        if(userProfile!=null){

            binding.name.text = userProfile!!.name

            if(userProfile!!.accType=="2"){
                binding.switchManager.isChecked = true
            }


            if(loggedUser!!.id != userProfile!!.id){
                binding.cardPassChange.visibility = View.GONE
            }else{
                binding.changePassword.setOnClickListener {
                    showChangePasswordDialog()
                }
            }

            binding.switchManager.setOnCheckedChangeListener { p0, value -> changeManager(value) }

//            Glide.with(this)
//                .load(userProfile!!.photoUrl)
//                .addListener()
//                .into(binding.profileImg)
        }


    }

    private fun changeManager(value: Boolean) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .changeManager(userProfile!!.id!! , Constant.booleanToInt(value))
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)
                        //binding.switchManager.isChecked = response.body()!!.error
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun showChangePasswordDialog() {
        val dialogBinding = DialogPasswordChangeBinding.inflate(layoutInflater);

        val builder= AlertDialog.Builder(this)
            .setCancelable(true)
            .setView(dialogBinding.root)

        val passDialog=builder.create()

        dialogBinding.btnChangePassword.setOnClickListener {
            val oldPass = dialogBinding.edtOldPass.text.toString()
            val newPass1 = dialogBinding.edtNewPass1.text.toString()
            val newPass2 = dialogBinding.edtNewPass2.text.toString()


            if(newPass1 != newPass2){
                shortToast("Password not matched")
                return@setOnClickListener
            }

            if(oldPass.length<6){
                shortToast("Old password must be grater than 5 character")
                return@setOnClickListener
            }

            if(newPass1.length<6){
                shortToast("New password must be grater than 5 character")
                return@setOnClickListener
            }

            loadingDialog.show()

            (application as MyApplication)
                .myApi
                .changePassword(oldPass, newPass1)
                .enqueue(object : Callback<GenericRespose> {
                    override fun onResponse(
                        call: Call<GenericRespose>,
                        response: Response<GenericRespose>) {

                        loadingDialog.hide()

                        if(response.isSuccessful && response.body()!=null){
                            shortToast(response.body()!!.msg)
                            if(!response.body()!!.error){
                                passDialog.dismiss()
                                startActivity(Intent(this@ProfileActivity, SplashActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                        loadingDialog.hide()
                    }

                })


        }

        passDialog.show()



    }
}