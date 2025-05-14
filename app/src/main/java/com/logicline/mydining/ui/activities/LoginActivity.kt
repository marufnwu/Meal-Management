package com.logicline.mydining.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.gson.Gson
import com.logicline.mydining.data.models.Support
import com.logicline.mydining.databinding.ActivityLoginBinding
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.MyApplication
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.Ext.MyExtensions.collectState
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "LoginActivity"

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val viewModel: UserViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        setupFlow()
        setupListeners()
        fetchSupportInfo()
    }

    private fun setupFlow() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectState(
                    lifecycleOwner = this@LoginActivity,
                    onLoading = {
                        loadingDialog.show()
                    },
                    onSuccess = {
                        loadingDialog.hide()
                        if (it != null) {
                            lifecycleScope.launch {
                                viewModel.saveUserDataLocally(it)
                                navigateToMainActivity()
                            }
                        } else {
                            shortToast("Login failed")
                        }
                    },
                    onError = { error ->
                        loadingDialog.hide()
                        shortToast(error)
                    },
                )
            }
        }


    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener { handleLogin() }

        binding.btnNewMess.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.txtPrivacyPolicy.setOnClickListener {
            Constant.openPrivacyPolicy(this)
        }

        binding.txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        // Uncomment below to enable language selector
        /*
        binding.txtLang.text = LangUtils.getFullLanguage(this, LangUtils.getLanguage(this))
        binding.txtLang.setOnClickListener {
            LanguageSelectorDialog.Builder(this)
                .build()
                .show()
        }
        */
    }

    private fun fetchSupportInfo() {
        (application as MyApplication).myApi.getSupport()
            .enqueue(object : Callback<Support> {
                override fun onResponse(call: Call<Support>, response: Response<Support>) {
                    val support = response.body()
                    if (response.isSuccessful && support != null) {
                        binding.cardContactUs.apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                when (support.type) {
                                    "whatsapp" -> Constant.openWpCustomerCare(
                                        this@LoginActivity,
                                        support.action
                                    )

                                    "link" -> Constant.openLink(this@LoginActivity, support.action)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Support>, t: Throwable) {
                    Log.e(TAG, "Failed to fetch support info", t)
                }
            })
    }

    private fun handleLogin() {
        val username = binding.edtUerName.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Every field is required", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(username, password)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}
