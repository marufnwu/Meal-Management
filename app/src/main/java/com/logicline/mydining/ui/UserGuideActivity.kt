package com.logicline.mydining.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.adapter.UserGuideAdapter
import com.logicline.mydining.databinding.ActivityUserGuideBinding
import com.logicline.mydining.models.UserGuide
import com.logicline.mydining.models.response.Paging
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserGuideActivity : BaseActivity(false) {
    lateinit var binding : ActivityUserGuideBinding
    private var userGuides: MutableList<UserGuide> = mutableListOf()
    lateinit var adapter: UserGuideAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var loadingDialog: LoadingDialog

    var pastVisibleItem: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0


    private var CURR_PAGE = 1
    private var TOTAL_PAGE = 0
    var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "User Guide"
        binding = ActivityUserGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog  =LoadingDialog(this)


        adapter  =UserGuideAdapter(this, userGuides)

        adapter.onAction = object: UserGuideAdapter.OnAction {
            override fun onClick(userGuide: UserGuide) {
                Constant.openLink(this@UserGuideActivity, userGuide.action_url)
            }

        }

        binding.recyUserGuide.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        binding.recyUserGuide.layoutManager = layoutManager
        binding.recyUserGuide.adapter = adapter

        initRecyScrollListener()
        getUserGuides(CURR_PAGE, TOTAL_PAGE)

    }

    private fun initRecyScrollListener() {

        binding.recyUserGuide.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItem = layoutManager.findFirstVisibleItemPosition()

                    if (dy > 0) {
                        Log.d("Pagination", "Scrolled")
                        if (!isLoading) {
                            if (CURR_PAGE <= TOTAL_PAGE) {
                                Log.d("Pagination", "Total Page $TOTAL_PAGE")
                                Log.d("Pagination", "Page $CURR_PAGE")
                                if (visibleItemCount + pastVisibleItem >= totalItemCount) {
                                    isLoading = true
                                    Log.v("...", "Last Item Wow !")
                                    CURR_PAGE++
                                    if (CURR_PAGE <= TOTAL_PAGE) {
                                        getUserGuides(
                                            currPage = CURR_PAGE,
                                            totalPage = TOTAL_PAGE
                                        )
                                    } else {
                                        isLoading = false
                                    }
                                }
                            } else {
                                Log.d("Pagination", "End of page")
                            }
                        } else {
                            Log.d("Pagination", "Loading")
                        }
                    }
                }
            })
    }

    private fun getUserGuides(currPage:Int, totalPage:Int){
        if(currPage==1){
            loadingDialog.show()
        }

        (application as MyApplication)
            .myApi
            .getAllUserGuide(currPage, totalPage)
            .enqueue(object: Callback<ServerResponse<Paging<UserGuide>>> {
                override fun onResponse(
                    call: Call<ServerResponse<Paging<UserGuide>>>,
                    response: Response<ServerResponse<Paging<UserGuide>>>
                ) {
                    isLoading = false
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            response.body()!!.data.let {
                                userGuides.addAll(it!!.data!!)
                                adapter.notifyDataSetChanged()
                                TOTAL_PAGE = it.totalPage
                            }
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<Paging<UserGuide>>>,
                    t: Throwable
                ) {
                    isLoading = false
                    loadingDialog.hide()
                }

            })


    }
}