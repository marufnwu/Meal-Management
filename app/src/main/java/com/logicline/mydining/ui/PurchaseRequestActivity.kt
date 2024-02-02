package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityPurchaseRequestBinding
import com.logicline.mydining.models.PurchaseRequest
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.ui.fragments.PurchaseRequestFragment
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PurchaseRequestActivity : BaseActivity() {
    lateinit var binding : ActivityPurchaseRequestBinding
    lateinit var loadingDialog: LoadingDialog

    val framgents = listOf(
        "Pending" to 0,
        "Accepted" to 1,
        "Rejected" to 2
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.purchase_request)


        binding.btnSubmitPurchase.setOnClickListener {
            startActivity(Intent(this, SubmitPurchaseActivity::class.java))
        }


        setViewpagerAdapter()

    }




    private fun setViewpagerAdapter() {


        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return framgents.size
            }

            override fun createFragment(position: Int): Fragment {
                return PurchaseRequestFragment.create(
                    framgents[position].second,
                )
            }

        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            tab.text = framgents[position].first
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return false
    }
}