package com.logicline.mydining.ui.custom_views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.logicline.mydining.R
import com.logicline.mydining.models.Banner
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.skyfishjy.library.RippleBackground
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BannerView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var enableAnimation = true
    private var banner :String? = null
    private val myView = this
    lateinit var ripple : RippleBackground
    lateinit var imgBanner : ImageView
    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.BannerView, 0, 0).apply {
            try {
                if(banner==null){
                    banner = getString(R.styleable.BannerView_banner)
                }
                enableAnimation = getBoolean(R.styleable.BannerView_enableAnimation, false)
            } finally {
                recycle()
            }
        }
        this.rootView.layoutParams = LayoutParams(10, 10)


        initView()

    }



    private fun showView() {
        this.visibility = View.VISIBLE
        ripple.startRippleAnimation()
        if(enableAnimation){
            toggle()
        }
    }

    private fun hideView() {
        this.visibility = View.GONE
        ripple.stopRippleAnimation()
    }

    @SuppressLint("Recycle")
    fun setBanner(b: String){
        banner = b
        getBanner()
    }



    private fun initView() {
        inflate(context, R.layout.layout_banner_view, this)

        this.visibility = View.GONE
        ripple = findViewById(R.id.rippleBg)
        imgBanner = findViewById(R.id.imgBanner)



        if(banner==null){
            hideView()
        }else{
            showView()
            getBanner()

        }
    }

    private fun getBanner(){

        showView()

        (context.applicationContext as MyApplication)
            .myApi
            .getBanner(banner!!)
            .enqueue(object : Callback<ServerResponse<Banner>> {
                override fun onResponse(
                    call: Call<ServerResponse<Banner>>,
                    res: Response<ServerResponse<Banner>>) {
                    if(res.isSuccessful && res.body()!=null){

                        if(!res.body()!!.error){
                            val bannerRes = res.body()!!.data!!
                            if(bannerRes.visible==1){

                                Glide.with(context)
                                    .load(bannerRes.imageUrl)
                                    .placeholder(R.drawable.loading)
                                    .into(imgBanner)

                                this@BannerView.setOnClickListener {
                                    Constant.openLink(context, bannerRes.actionUrl)
                                }
                            }else{
                                hideView()
                            }
                        }else{
                            hideView()
                        }


                    }
                }

                override fun onFailure(call: Call<ServerResponse<Banner>>, t: Throwable) {
                    hideView()
                }

            })




    }

    private fun toggle() {
        val slideUp: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_left)
        this.startAnimation(slideUp)
    }

}