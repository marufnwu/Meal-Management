package com.logicline.mydining.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.MonthAdapter
import com.logicline.mydining.databinding.ActivityActiveMonthBinding
import com.logicline.mydining.databinding.FragmentMonthListBinding
import com.logicline.mydining.models.MonthOfYear
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.ui.fragments.MONTH_TYPE
import com.logicline.mydining.ui.fragments.MonthListFragment
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActiveMonthActivity : BaseActivity() {
    private lateinit var binding : ActivityActiveMonthBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var adapter: MonthAdapter
    private var months: MutableList<MonthOfYear> = mutableListOf()
    lateinit var myFullScreenAd: MyFullScreenAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.month_list)
        binding = ActivityActiveMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myFullScreenAd = MyFullScreenAd(this, true)

        loadingDialog  = LoadingDialog(this)

        initViews()


    }

    private fun initViews() {

        val items = listOf<Item>(
            Item(MonthListFragment.newInstance(MONTH_TYPE.AUTO), "Auto"),
            Item(MonthListFragment.newInstance(MONTH_TYPE.MANUAL), "Manual"),

        )

        val adapter = PagerAdapter(items, supportFragmentManager)
        binding.pager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.pager)
    }



    override fun onBackPressed() {
        myFullScreenAd.showAd()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            onBackPressed()
            return true
        }
        return false
    }

    data class Item(val fragment : Fragment, val title : String)


    class PagerAdapter(val items : List<Item>, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int  = items.size

        override fun getItem(i: Int): Fragment {
            return items[i].fragment
        }

        override fun getPageTitle(position: Int): CharSequence {
            return items[position].title
        }
    }

}