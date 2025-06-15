package com.logicline.mydining.ui.fragments.menus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.MyApplication
import com.logicline.mydining.R
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.data.enums.PurchaseType
import com.logicline.mydining.data.models.Banner
import com.logicline.mydining.data.models.Support
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.UserGuide
import com.logicline.mydining.data.models.response.InitialDataResponse
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.databinding.FragmentHomeBinding
import com.logicline.mydining.ui.SmoothScrollBehavior
import com.logicline.mydining.ui.activities.*
import com.logicline.mydining.ui.adapter.MainSliderAdapter
import com.logicline.mydining.ui.custom.monthpicker.MonthPickerBottomSheet
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.AppPrefs
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.Ext.MyExtensions.handle
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.maruf.jdialog.JDialog
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment() {
    // Material theme color references
    private var expandedColorInt: Int = 0
    private var collapsedColorInt: Int = 0

    // State tracking
    private var isBannerVisible = true
    private var lastScrollRatio = 0f

    // View binding property
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // MainActivity converted properties
    private var userData: UserData? = null
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mainBottomSheet: MainBottomSheet

    private var userGuideBanner: MutableLiveData<Banner> = MutableLiveData()
    private val userViewModel: UserViewModel by viewModels()

    private var rotationAngle = 0f
    private var isUserGuideExpand = false
    private var layoutRefresh: SwipeRefreshLayout? = null
    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    companion object {
        const val HOME_MAIN_BANNER: String = "homeMain"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())
        mainBottomSheet = MainBottomSheet.Companion.newInstance()

        setupAppBarBehavior()
        setupWindowInsets()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            AppPrefs.monthFlow.collect {
                // Update the month header UI
                it?.let { month ->
                    binding.txtMonthName?.text = month.name

                    // Format the period (start date - end date)
                    // Format and set the period dates
                    val startDate = month.startAt.let { dateString ->
                        try {
                            inputDateFormat.parse(dateString)?.let { parsedDate -> outputDateFormat.format(parsedDate) }
                        } catch (e: Exception) {
                            android.util.Log.e("SummaryActivity", "Error formatting start date", e)
                            null
                        }
                    } ?: "N/A"

                    val endDate = month.endAt?.let { dateString ->
                        try {
                            inputDateFormat.parse(dateString)?.let { parsedDate -> outputDateFormat.format(parsedDate) }
                        } catch (e: Exception) {
                            android.util.Log.e("SummaryActivity", "Error formatting end date", e)
                            null
                        }
                    } ?: "Ongoing"

                    binding.txtMonthPeriod?.text = "$startDate - $endDate"

                    // Update status indicator
                    binding.txtMonthStatus?.text = if (month.isActive) "Active" else "Inactive"
                    binding.txtMonthStatus?.setBackgroundResource(
                        if (month.isActive) R.drawable.status_background
                        else R.drawable.status_background_inactive
                    )
                }

                userViewModel.loadUserMinimalMonthSummary()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            userViewModel.userMinimalSummaryState.collect {
                it.handle(
                    onLoading = {
                        loadingDialog.show()
                    },
                    onSuccess = {
                        loadingDialog.hide()
                        Log.d(TAG, "onViewCreated: user summary " + Gson().toJson(it))
                        binding.txtMealcharge.text = it?.summary?.mealCharge.toString()
                        binding.txtTotalMeal.text = it?.summary?.totalMeal.toString()
                        binding.txtBalance.text = it?.summary?.balance.toString()
                    },
                    onError = { msg ->
                        loadingDialog.hide()
                        requireContext().shortToast(msg)
                    },
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            userViewModel.observedUserData.collect { state ->
                state.handle(
                    onLoading = {
                        loadingDialog.show()
                    },
                    onSuccess = {
                        loadingDialog.hide()
                        userData = it
                        Log.d(TAG, "onViewCreated: user data " + Gson().toJson(userData))
                        if (userData == null) {
                            Log.d(TAG, "onViewCreated: user data " + Gson().toJson(userData))
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finish()
                        }

                        userData?.user?.let { user ->
                            user.phone?.let { phone ->
                                binding.txtUserPhone.text = phone
                            }

                            user.name?.let { name ->
                                binding.txtUserName.text = name
                            }

                            user.photoUrl?.let { photoUrl ->
                                Glide.with(requireContext())
                                    .load(BuildConfig.BASE_URL + photoUrl)
                                    .into(binding.imgProPic)
                            }
                        }

                        // Check permissions for button visibility
                        if (!userData?.messUser.hasAnyPermission(MessPermission.MEAL_ADD, MessPermission.MEAL_MANAGEMENT)) {
                            // Find addMeal view in menuGrid if it exists
                            // This would need to be handled in setupMenuGrid
                        }

                        if (!userData?.messUser.hasAnyPermission(MessPermission.USER_MANAGEMENT)) {
                            // Find any permission related views and hide them
                        }

                        userViewModel.loadUserMinimalMonthSummary()

                        askNotificationPermission()
                        setupClickListeners()
                        getInitialData()
                        getHomeMainBanner()
                        registerFcm()
                        updateFcmToken()
                        getSliderData()
                    }
                )
            }
        }





    }

    private fun setupAppBarBehavior() {
        // Initialize colors from theme attributes
        val typedValue = TypedValue()

        // Get primaryContainer color for expanded state
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryContainer,
            typedValue,
            true
        )
        expandedColorInt = typedValue.data
        val expandedColor = android.content.res.ColorStateList.valueOf(expandedColorInt)

        // Get primary color for collapsed state
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            typedValue,
            true
        )
        collapsedColorInt = typedValue.data
        val collapsedColor = android.content.res.ColorStateList.valueOf(collapsedColorInt)

        // Set initial state
        binding.headerBackground.setCardBackgroundColor(expandedColor)
        activity?.window?.statusBarColor = expandedColorInt

        // Setup smooth scrolling behavior
        val params = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = SmoothScrollBehavior(requireContext(), null)
        binding.appBarLayout.layoutParams = params

        // Setup AppBarLayout offset listener for color transitions
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
            // Calculate scroll percentage (0 = fully expanded, 1 = fully collapsed)
            val totalScrollRange = appBar.totalScrollRange.toFloat()
            val scrollRatio = (abs(verticalOffset) / totalScrollRange).coerceIn(0f, 1f)

            // Handle UI transitions based on scroll
            handleColorTransition(binding.headerBackground, scrollRatio, expandedColor, collapsedColor)
            handleBannerVisibility(binding.promoBanner, scrollRatio)
            handleLocationVisibility(binding.llMonth, scrollRatio)

            lastScrollRatio = scrollRatio
        })

        setupMenuGrid()
    }

    private fun setupWindowInsets() {
        // Store the original toolbar height
        val originalToolbarHeight = binding.toolbar.layoutParams.height

        // Apply window insets to handle edge-to-edge display correctly
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { _, windowInsets ->
            val statusBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            val displayCutoutInsets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout())

            // Get the maximum top inset (considering both status bar and any cutouts)
            val topInset = maxOf(statusBarInsets.top, displayCutoutInsets.top)

            // Apply insets to toolbar while preserving its original height
            binding.toolbar.updatePadding(
                top = topInset
            )

            // Adjust toolbar height to account for status bar
            binding.toolbar.updateLayoutParams {
                height = originalToolbarHeight + topInset
            }

            // Apply window insets to the whole AppBarLayout
            ViewCompat.onApplyWindowInsets(
                binding.appBarLayout,
                WindowInsetsCompat.Builder()
                    .setInsets(
                        WindowInsetsCompat.Type.systemBars(),
                        androidx.core.graphics.Insets.of(0, topInset, 0, 0)
                    )
                    .build()
            )

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun handleColorTransition(
        headerBackground: com.google.android.material.card.MaterialCardView,
        scrollRatio: Float,
        expandedColor: android.content.res.ColorStateList,
        collapsedColor: android.content.res.ColorStateList
    ) {
        // Constants for transition thresholds
        val MIN_THRESHOLD = 0.05f
        val MAX_THRESHOLD = 0.95f

        when {
            scrollRatio <= MIN_THRESHOLD -> {
                // Fully expanded
                headerBackground.setCardBackgroundColor(expandedColor)
                activity?.window?.statusBarColor = expandedColorInt
            }
            scrollRatio >= MAX_THRESHOLD -> {
                // Fully collapsed
                headerBackground.setCardBackgroundColor(collapsedColor)
                activity?.window?.statusBarColor = collapsedColorInt
            }
            else -> {
                // Calculate normalized ratio for smoother transition
                val normalizedRatio = (scrollRatio - MIN_THRESHOLD) / (MAX_THRESHOLD - MIN_THRESHOLD)
                val color = blendColors(expandedColorInt, collapsedColorInt, normalizedRatio)
                headerBackground.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(color))
                activity?.window?.statusBarColor = color
            }
        }
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        // Using Android's ArgbEvaluator for efficient color blending
        return android.animation.ArgbEvaluator().evaluate(ratio, color1, color2) as Int
    }

    private fun handleBannerVisibility(promoBanner: View, scrollRatio: Float) {
        val VISIBILITY_THRESHOLD = 0.3f
        val shouldBeVisible = scrollRatio < VISIBILITY_THRESHOLD

        if (shouldBeVisible != isBannerVisible) {
            isBannerVisible = shouldBeVisible

            // Use property animator for better performance
            promoBanner.animate().cancel() // Cancel any ongoing animations

            if (shouldBeVisible) {
                promoBanner.visibility = View.VISIBLE
                promoBanner.translationY = promoBanner.height.toFloat()
                promoBanner.animate()
                    .translationY(0f)
                    .setDuration(150)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            } else {
                promoBanner.animate()
                    .translationY(promoBanner.height.toFloat())
                    .setDuration(150)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        if (!isBannerVisible) {
                            promoBanner.visibility = View.INVISIBLE
                        }
                    }
                    .start()
            }
        } else if (shouldBeVisible && promoBanner.visibility != View.VISIBLE) {
            // Ensure visibility is correct if state is unchanged but view is invisible
            promoBanner.visibility = View.VISIBLE
            promoBanner.translationY = 0f
        }
    }

    private fun handleLocationVisibility(locationLayout: LinearLayout?, scrollRatio: Float) {
        // Constants for fade thresholds
        val START_FADE = 0.15f
        val END_FADE = 0.80f

        locationLayout?.let { layout ->
            when {
                scrollRatio <= START_FADE -> {
                    // Fully visible
                    layout.alpha = 1f
                    layout.visibility = View.VISIBLE
                    layout.translationY = 0f
                }
                scrollRatio >= END_FADE -> {
                    // Fully hidden
                    layout.alpha = 0f
                    layout.visibility = View.INVISIBLE
                }
                else -> {
                    // Calculate fade progress
                    val fadeProgress = (scrollRatio - START_FADE) / (END_FADE - START_FADE)
                    layout.alpha = 1f - fadeProgress
                    layout.visibility = View.VISIBLE

                    // Slide up as we scroll
                    val slideDistance = layout.height * 0.2f * fadeProgress
                    layout.translationY = -slideDistance
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }

    // Methods converted from MainActivity
    private fun getSliderData() {
        (requireActivity().application as MyApplication)
            .myApi
            .getMainSlider()
            .enqueue(object : Callback<ServerResponse<MutableList<UserGuide>>> {
                override fun onResponse(
                    call: Call<ServerResponse<MutableList<UserGuide>>>,
                    response: Response<ServerResponse<MutableList<UserGuide>>>
                ) {
                    if (_binding == null) return // Fragment view destroyed

                    response.body()?.let {
                        if (!it.error) {
                            it.data?.let { guides ->
                                if (guides.size > 0) {
                                    binding.slider.visibility = View.VISIBLE
                                    val sliderAdapter = MainSliderAdapter(requireContext(), guides)
                                    binding.slider.setSliderAdapter(sliderAdapter)
                                    binding.slider.isAutoCycle = true
                                }
                            }
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<MutableList<UserGuide>>>,
                    t: Throwable
                ) {
                    // Handle failure
                }
            })
    }

    private fun getHomeMainBanner() {
        (requireActivity().application as MyApplication)
            .myApi
            .getBanner(HOME_MAIN_BANNER)
            .enqueue(object : Callback<ServerResponse<Banner>> {
                override fun onResponse(
                    call: Call<ServerResponse<Banner>>,
                    res: Response<ServerResponse<Banner>>
                ) {
                    if (res.isSuccessful && res.body() != null) {
                        if (!res.body()!!.error) {
                            val bannerRes = res.body()!!.data!!
                            userGuideBanner.postValue(bannerRes)
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Banner>>, t: Throwable) {
                    // Handle failure
                }
            })
    }

    private fun updateFcmToken() {
        // Firebase messaging token update logic
        // Implement when needed
    }

    private fun registerFcm() {
        // Firebase messaging subscription logic
        // Implement when needed
    }

    private fun getInitialData() {
        loadingDialog.show()
        (requireActivity().application as MyApplication)
            .myApi
            .getInitialData(BuildConfig.VERSION_CODE)
            .enqueue(object : Callback<InitialDataResponse> {
                override fun onResponse(
                    call: Call<InitialDataResponse>,
                    response: Response<InitialDataResponse>
                ) {
                    if (_binding == null) return // Fragment view destroyed

                    loadingDialog.hide()
                    if (response.isSuccessful) {
                        response.body()?.let { initialDataResponse ->
                            if (!initialDataResponse.error) {
                                initialDataResponse.initialData?.let {
                                    binding.txtMealcharge.text = it.mealCharge
                                    binding.txtTotalMeal.text = it.totalMeal

                                    LocalDB.saveInitialData(it)

                                    setCustomerSupport(it.support)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<InitialDataResponse>, t: Throwable) {
                    if (_binding == null) return // Fragment view destroyed

                    requireContext().shortToast(t.message)
                    loadingDialog.hide()

                    JDialog.make(requireContext())
                        .setCancelable(false)
                        .setBodyText("Something went wrong! Please try again.")
                        .setIconType(JDialog.IconType.ERROR)
                        .setPositiveButton("Try Again") {
                            it.hideDialog()
                            getInitialData()
                        }.build()
                        .showDialog()
                }
            })
    }

    private fun setCustomerSupport(support: Support?) {
        if (_binding == null) return // Fragment view destroyed

        support?.let {
            if (it.active) {
                binding.imgCust.visibility = View.VISIBLE
                // This view might be missing in your layout
                // binding.cardContactUs.visibility = View.VISIBLE

                // binding.cardContactUs.setOnClickListener {
                //     if (support.type == "whatsapp") {
                //         Constant.openWpCustomerCare(requireContext(), support.action)
                //     } else if (support.type == "link") {
                //         Constant.openLink(requireContext(), support.action)
                //     }
                // }

                binding.imgCust.setOnClickListener {
                    if (support.type == "whatsapp") {
                        Constant.openWpCustomerCare(requireContext(), support.action)
                    } else if (support.type == "link") {
                        Constant.openLink(requireContext(), support.action)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {



        binding.llMonth?.setOnClickListener {
            // Open month selection bottom sheet
            MonthPickerBottomSheet.show(
                fragmentManager = childFragmentManager, // or parentFragmentManager or activity's supportFragmentManager
                preselectedMonthId = AppPrefs.monthId
            ) { month ->

                AppPrefs.month = month // Save selected month
                // Update your UI here if needed
                // For example, update a text view with the selected month name
            }
        }


        // Setup refresh listener if SwipeRefreshLayout exists
        if (view?.findViewById<SwipeRefreshLayout>(R.id.layoutRefresh) != null) {
            layoutRefresh = view?.findViewById(R.id.layoutRefresh)
            layoutRefresh?.setOnRefreshListener {
                refreshData()
            }
        }

//        userGuideBanner.observe(viewLifecycleOwner) {
//            if (it != null && view?.findViewById<ImageView>(R.id.imgUserGuide) != null) {
//                Constant.setBanner(view?.findViewById(R.id.imgUserGuide), it, requireContext())
//
//                if (LocalDB.isFirstOpen()) {
//                    toggleUserGuide()
//                    LocalDB.setFirstOpen(false)
//                }
//            }
//        }

        // Setup all quick access menu click listeners
        setupQuickAccessMenuListeners()

        // Setup drawer listener
        binding.imgDrawer.setOnClickListener {
            if (childFragmentManager.findFragmentByTag("MainBottomSheet") == null) {
                mainBottomSheet.show(childFragmentManager, "MainBottomSheet")
            }
        }

        // Settings icon
        binding.imgSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        // Profile picture
        binding.imgProPic.setOnClickListener {
            userData?.user?.let { user ->
                startActivity(Intent(requireContext(), ProfileActivity::class.java)
                    .putExtra("profile", user)
                )
            }
        }

        // FAQ link if available
        view?.findViewById<View>(R.id.layoutFaq)?.setOnClickListener {
            Constant.openWebView(
                "Frequently ask question",
                BuildConfig.BASE_URL + "faq.html",
                requireContext()
            )
        }

        // User guide toggle if available
        view?.findViewById<View>(R.id.layoutUserGideToggle)?.setOnClickListener {
            toggleUserGuide()
        }
    }

    private fun setupQuickAccessMenuListeners() {
        // Quick access menu items
        binding.mealChart.setOnClickListener {
            startActivity(Intent(requireContext(), MealActivity::class.java))
        }

        binding.purchases.setOnClickListener {
            startActivity(
                Intent(requireContext(), PurchasesActivity::class.java)
                    .putExtra(Constant.PURCHASE_TYPE, PurchaseType.MEAL.name)
            )
        }

        binding.otherCost.setOnClickListener {
            startActivity(
                Intent(requireContext(), PurchasesActivity::class.java)
                    .putExtra(Constant.PURCHASE_TYPE, PurchaseType.OTHER.name)
            )
        }

        binding.deposit.setOnClickListener {
            startActivity(Intent(requireContext(), DepositActivity::class.java))
        }

        // Add other menu items per your layout
        // These views might need to be added to your menuGrid dynamically
        // We'll need additional code for:
        // - addMeal
        // - members
        // - initiateMember
        // - addPurchase
        // - purchaseRequest
        // - oldData
        // etc.
    }

    private fun refreshData() {
        // Reload all data
        userViewModel.loadUserMinimalMonthSummary()
        getInitialData()
        getHomeMainBanner()
        getSliderData()
        layoutRefresh?.isRefreshing = false
    }

    private fun toggleUserGuide() {
        val imgDownExpand = view?.findViewById<ImageView>(R.id.imgDownExpand) ?: return
        val layoutUserGuide = view?.findViewById<View>(R.id.layoutUserGuide) ?: return

        rotationAngle = if (rotationAngle == 0f) 180f else 0f // toggle
        imgDownExpand.animate().rotation(rotationAngle).setDuration(500).start()

        if (isUserGuideExpand) {
            Constant.collapse(layoutUserGuide)
        } else {
            Constant.expand(layoutUserGuide)
        }

        isUserGuideExpand = !isUserGuideExpand
    }

    private fun askNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale if needed
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result
    }

    override fun startActivity(intent: Intent) {
        requireActivity().startActivity(intent)
    }

    private fun setupMenuGrid() {
        val menuItems = MenuItem.menuItems

        // Create menu items programmatically
        menuItems.forEachIndexed { index, item ->
            // Inflate the menu item layout
            val itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.menu_grid_item, binding.menuGrid, false)

            // Get views
            val iconView = itemView.findViewById<ImageView>(R.id.menuItemIcon)
            val titleView = itemView.findViewById<TextView>(R.id.menuItemTitle)

            // Set icon and apply tint color
            iconView.setImageResource(item.iconResId)
            iconView.setColorFilter(Color.parseColor(item.colorAttr))

            // Set title
            titleView.setText(item.titleResId)

            // Set click listener if needed
             itemView.setOnClickListener {
                when (item.id) {
                    "members" -> startActivity(Intent(requireContext(), MembersActivity::class.java))
                    "initiateMember" -> startActivity(Intent(requireContext(), InitiateMemberActivity::class.java))
                    "addMeal" -> startActivity(Intent(requireContext(), AddMealActivity::class.java))
                    "addPurchase" -> startActivity(Intent(requireContext(), AddPurchaseActivity::class.java)
                        .putExtra(Constant.PURCHASE_TYPE, PurchaseType.MEAL.name))
                    "addFund" -> startActivity(Intent(requireContext(), FundActivity::class.java))
                    "purchaseRequest" -> startActivity(Intent(requireContext(),
                        PurchaseRequestActivity::class.java)
                        .putExtra(Constant.PURCHASE_TYPE, PurchaseType.MEAL.name))
                    "summary" -> startActivity(Intent(requireContext(), SummaryActivity::class.java))
                    "oldData" -> startActivity(Intent(requireContext(), PreviousMonthActivity::class.java))
                }
             }

            // Add layout parameters
            val params = GridLayout.LayoutParams()
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
            params.width = 0
            itemView.layoutParams = params

            // Add view to grid
            binding.menuGrid.addView(itemView)
        }
    }

    data class MenuItem(val id: String, val titleResId: Int, val iconResId: Int, val colorAttr: String) {
        companion object {
            val menuItems = listOf(
                MenuItem("members", R.string.members, R.drawable.users, "#FF6B81"),
                MenuItem("initiateMember", R.string.initiate_members, R.drawable.start_24px, "#5758BB"),
                MenuItem("addMeal", R.string.add_meal, R.drawable.set_meal_24px, "#2ECC71"),
                MenuItem("addPurchase", R.string.add_purchase, R.drawable.shopping_bag_24px, "#FA8231"),
                MenuItem("addFund", R.string.fund, R.drawable.fund, "#3498DB"),
                MenuItem("purchaseRequest", R.string.purchase_request, R.drawable.cart, "#9B59B6"),
                MenuItem("summary", R.string.monthly_summary, R.drawable.summarize_24px, "#38ADA9"),
                MenuItem("oldData", R.string.previous_data, R.drawable.clock_arrow_down_24px, "#E84393")
            )
            fun getItemById(id: String): MenuItem? {
                return menuItems.find { it.id == id }
            }
        }
    }
}

