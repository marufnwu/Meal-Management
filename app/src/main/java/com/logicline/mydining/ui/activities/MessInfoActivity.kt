package com.logicline.mydining.ui.activities

import android.os.Bundle
import android.widget.EditText
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityMessInfoBinding
import com.logicline.mydining.data.enums.MessStatus
import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.MyApplication
import com.logicline.mydining.ui.custom.GenericDialog
import com.logicline.mydining.ui.custom.StatusView
import com.logicline.mydining.ui.viewmodels.MonthViewModel
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.logicline.mydining.data.DataState
import com.logicline.mydining.ui.viewmodels.MessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

@AndroidEntryPoint
class MessInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityMessInfoBinding
    private lateinit var loadingDialog: LoadingDialog
    private  var messCreateDialog : GenericDialog? = null

    private val viewModel : MessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.mess_info)
        binding = ActivityMessInfoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        //getMessInfo()
        setData(LocalDB.getUserData()?.messUser?.mess)

        initViews()

        setCollectors()
    }

    private fun setCollectors() {
        // Optionally observe the result (with Flow or LiveData)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createMessState.collect { state ->
                    when (state) {
                        is DataState.Loading -> {
                            loadingDialog.show()
                        }
                        is DataState.Success -> {
                            loadingDialog.hide()
                            messCreateDialog?.dismiss()
                        }
                        is DataState.Error ->{
                            loadingDialog.hide()
                            shortToast(state.message)
                        }
                        else -> Unit
                    }
                }
            }
        }

    }

    private fun initViews() {
        binding.statusView.setPositiveButton(listener = object : StatusView.OnPositiveButtonClickListener{
            override fun onPositiveButtonClick() {
                showCreateMessDialog()
            }



        })
    }
    private fun createMess(name: String) {
        viewModel.createMess(name)
    }
    private fun showCreateMessDialog() {
         messCreateDialog = GenericDialog.Builder(this) .setIcon(R.drawable.add)
            .setTitle("Create Mess!")
            .setPositiveButton("Add to Cart", object : GenericDialog.OnClickListener{
                override fun onClick(genericDialog: GenericDialog) {

                    val name = genericDialog.findViewById<EditText>(R.id.ev_mess_name)

                    createMess(name!!.text.toString())

                }



            })
            .setNegativeButton("Cancel")
            .setAutoDismiss(false)
            .setContentView(R.layout.layout_create_mess)
            .show()

    }


    private fun getMessInfo() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getMessInfo()
            .enqueue(object: Callback<ServerResponse<Mess>> {
                override fun onResponse(
                    call: Call<ServerResponse<Mess>>,
                    response: Response<ServerResponse<Mess>>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            setData(response.body()!!.data)
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Mess>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun setData(data: Mess?) {


        if(data!=null){
            data.let {
                binding.txtMessName.text = data.name
                binding.txtMessId.text = data.id.toString()
                binding.txtMessCreated.text = data.createdAt.toString()
                binding.txtStatus.text = MessStatus.fromValue(data.status)?.value?: "Undefined"
            }
        }else{
            binding.statusView.setStatus(StatusView.StatusType.EMPTY, "No Mess Found! Please create a mess or join existing mess.")
            binding.statusView.showStatusView()
        }




    }
}