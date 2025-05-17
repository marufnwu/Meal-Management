package com.logicline.mydining.ui.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.card.MaterialCardView
import com.logicline.mydining.R
import com.logicline.mydining.ui.SmoothScrollBehavior
import kotlin.math.abs

private const val TAG = "DemoActivity"

class DemoActivity : AppCompatActivity() {
    // Material theme color references
    private lateinit var expandedColor: ColorStateList
    private lateinit var collapsedColor: ColorStateList
    private var expandedColorInt: Int = 0ork
    private var collapsedColorInt: Int = 0

    // State tracking
    private var isBannerVisible = true
    private var lastScrollRatio = 0f
    private var colorAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_demo)

        // Initialize colors from theme attributes
        val typedValue = TypedValue()

        // Initialize Material theme colors
        // Get primaryContainer color for expanded state
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryContainer,
            typedValue,
            true
        )
        expandedColorInt = typedValue.data
        expandedColor = ColorStateList.valueOf(expandedColorInt)

        // Get primary color for collapsed state
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        collapsedColorInt = typedValue.data
        collapsedColor = ColorStateList.valueOf(collapsedColorInt)
        expandedColor = ColorStateList.valueOf(expandedColorInt)
        collapsedColor = ColorStateList.valueOf(collapsedColorInt)

        // Set initial status bar color
        window.statusBarColor = expandedColorInt

        val appBarLayout = findViewById<AppBarLayout>(R.id.appBarLayout)
        val headerBackground = findViewById<MaterialCardView>(R.id.header_background)
        val promoBanner = findViewById<View>(R.id.promo_banner)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val locationLayout = findViewById<LinearLayout>(R.id.ll_location)

        // Set initial state
        headerBackground.setCardBackgroundColor(expandedColor)

        // Setup smooth scrolling behavior
        val params = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = SmoothScrollBehavior(this, null)
        appBarLayout.layoutParams = params

        // Store the original toolbar height before we apply any insets
        val originalToolbarHeight = toolbar.layoutParams.height

        // Apply window insets to handle edge-to-edge display correctly
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { view, windowInsets ->
            val statusBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            val displayCutoutInsets =
                windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout())

            // Get the maximum top inset (considering both status bar and any cutouts)
            val topInset = maxOf(statusBarInsets.top, displayCutoutInsets.top)

            // Apply insets to toolbar while preserving its original height
            toolbar.updatePadding(
                top = topInset
            )

            // Adjust toolbar height to account for status bar
            toolbar.updateLayoutParams {
                height = originalToolbarHeight + topInset
            }

            // The key change: Apply window insets to the whole AppBarLayout
            // This prevents it from going under the status bar
            ViewCompat.onApplyWindowInsets(
                appBarLayout,
                WindowInsetsCompat.Builder()
                    .setInsets(
                        WindowInsetsCompat.Type.systemBars(),
                        androidx.core.graphics.Insets.of(0, topInset, 0, 0)
                    )
                    .build()
            )

            // Return insets for potential consumption by other views
            WindowInsetsCompat.CONSUMED
        }

        // Improved offset listener for handling both color and banner visibility
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
            // Calculate scroll percentage (0 = fully expanded, 1 = fully collapsed)
            val totalScrollRange = appBar.totalScrollRange.toFloat()
            val scrollRatio = (abs(verticalOffset) / totalScrollRange).coerceIn(0f, 1f)

            Log.d(TAG, "onCreate: ")

            // Immediately handle color changes
            handleColorTransition(headerBackground, scrollRatio)

            // Handle banner visibility separately
            handleBannerVisibility(promoBanner, scrollRatio)

            // Handle location layout visibility with gradual fade
            handleLocationVisibility(locationLayout, scrollRatio)

            // Update last known position
            lastScrollRatio = scrollRatio
        })

        setupMenuGrid()
    }

    private fun handleColorTransition(headerBackground: MaterialCardView, scrollRatio: Float) {
        // Cancel any ongoing animation
        colorAnimator?.cancel()

        // For extreme positions, set colors directly
        when {
            scrollRatio <= 0.05f -> {
                headerBackground.setCardBackgroundColor(expandedColor)
                window.statusBarColor = expandedColorInt
                return
            }

            scrollRatio >= 0.95f -> {
                headerBackground.setCardBackgroundColor(collapsedColor)
                window.statusBarColor = collapsedColorInt
                return
            }
        }

        // For intermediate positions, calculate color
        val color = blendColors(expandedColorInt, collapsedColorInt, scrollRatio)
        headerBackground.setCardBackgroundColor(ColorStateList.valueOf(color))
        window.statusBarColor = color
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val evaluator = ArgbEvaluator()
        return evaluator.evaluate(ratio, color1, color2) as Int
    }

    private fun handleBannerVisibility(promoBanner: View, scrollRatio: Float) {
        val visibilityThreshold = 0.3f
        val shouldBeVisible = scrollRatio < visibilityThreshold

        if (shouldBeVisible != isBannerVisible) {
            isBannerVisible = shouldBeVisible

            if (shouldBeVisible) {
                // Make visible immediately to avoid visual delays
                promoBanner.visibility = View.VISIBLE
                promoBanner.translationY = promoBanner.height.toFloat()

                // Animate into view
                promoBanner.animate()
                    .translationY(0f)
                    .setDuration(150)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            } else {
                // Already visible, animate out
                promoBanner.animate()
                    .translationY(promoBanner.height.toFloat())
                    .setDuration(150)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        // Only set invisible if still supposed to be invisible
                        if (!isBannerVisible) {
                            promoBanner.visibility = View.INVISIBLE
                        }
                    }
                    .start()
            }
        } else if (shouldBeVisible && promoBanner.visibility != View.VISIBLE) {
            // Safety: Force visibility if it should be visible but isn't
            promoBanner.visibility = View.VISIBLE
            promoBanner.translationY = 0f
        }
    }

    private fun handleLocationVisibility(locationLayout: View, scrollRatio: Float) {
        // Configure thresholds for fade effect - adjust these values as needed
        val startFadeThreshold = 0.15f  // Start fading at 15% scrolled
        val endFadeThreshold = 0.80f    // Completely faded at 80% scrolled

        when {
            scrollRatio <= startFadeThreshold -> {
                // Fully visible when expanded
                locationLayout.alpha = 1f
                locationLayout.visibility = View.VISIBLE
            }

            scrollRatio >= endFadeThreshold -> {
                // Completely gone when collapsed beyond threshold
                locationLayout.alpha = 0f
                locationLayout.visibility = View.INVISIBLE
            }

            else -> {
                // Calculate fade progress between thresholds
                val fadeProgress =
                    (scrollRatio - startFadeThreshold) / (endFadeThreshold - startFadeThreshold)

                // Apply alpha gradually (1.0 to 0.0)
                locationLayout.alpha = 1f - fadeProgress

                // Keep visible while fading
                locationLayout.visibility = View.VISIBLE

                // Optionally slide the view up slightly as it fades
                val slideDistance = locationLayout.height * 0.2f * fadeProgress
                locationLayout.translationY = -slideDistance
            }
        }
    }

    private fun setupMenuGrid() {
        val gridLayout = findViewById<GridLayout>(R.id.menuGrid)

        val menuItems = MenuItem.menuItems

        // Create menu items programmatically
        menuItems.forEachIndexed { index, item ->
            // Inflate the menu item layout
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.menu_grid_item, gridLayout, false)

            // Get views
            val iconView = itemView.findViewById<ImageView>(R.id.menuItemIcon)
            val titleView = itemView.findViewById<TextView>(R.id.menuItemTitle)

            // Set icon and apply tint color
            iconView.setImageResource(item.iconResId)
            iconView.setColorFilter(Color.parseColor(item.colorAttr))

            // Set title
            titleView.setText(item.titleResId)


            // Set click listener
//            itemView.setOnClickListener {
//                handleMenuItemClick(index)
//            }

            // Add layout parameters
            val params = GridLayout.LayoutParams()
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
            params.width = 0
            itemView.layoutParams = params

            // Add view to grid
            gridLayout.addView(itemView)
        }


    }

    data class MenuItem(val id: String, val titleResId: Int, val iconResId: Int, val colorAttr: String) {
        companion object {
            // Define menu items with their icons and hex color strings
            val menuItems = listOf(
                // Row 1
                MenuItem("members", R.string.members, R.drawable.users, "#FF6B81"), // Pink
                MenuItem("initiateMember", R.string.initiate_members, R.drawable.start_24px, "#5758BB"), // Purple
                MenuItem("addMeal", R.string.add_meal, R.drawable.set_meal_24px, "#2ECC71"), // Green
                MenuItem("addPurchase", R.string.add_purchase, R.drawable.shopping_bag_24px, "#FA8231"), // Orange

                // Row 2
                MenuItem("addFund", R.string.fund, R.drawable.fund, "#3498DB"), // Blue
                MenuItem("purchaseRequest", R.string.purchase_request, R.drawable.cart, "#9B59B6"), // Purple
                MenuItem("summary", R.string.monthly_summary, R.drawable.summarize_24px, "#38ADA9"), // Teal
                MenuItem("oldData", R.string.previous_data, R.drawable.clock_arrow_down_24px, "#E84393")  // Pink
            )

            // Convenience method to get MenuItem by ID
            fun getItemById(id: String): MenuItem? {
                return menuItems.find { it.id == id }
            }
        }
    }
}