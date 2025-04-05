package com.logicline.mydining.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.logicline.mydining.R
import com.logicline.mydining.models.Month
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthsActivity : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_months)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadingDialog = LoadingDialog(this)

    }


}