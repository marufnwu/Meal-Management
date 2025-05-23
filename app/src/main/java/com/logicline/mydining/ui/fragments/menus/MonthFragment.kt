package com.logicline.mydining.ui.fragments.menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.logicline.mydining.MyApplication
import com.logicline.mydining.R
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.databinding.FragmentMonthBinding
import com.logicline.mydining.utils.AppPrefs
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.setStatusBarColorWithLifecycle


class MonthFragment : Fragment() {

    // Define the binding variable
    private var _binding: FragmentMonthBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the binding
        _binding = FragmentMonthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColorWithLifecycle(android.R.color.transparent, true)

        setupWindowInsets()
        initViews()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up binding when view is destroyed
        _binding = null
    }

    private fun initViews(){
        // Proper way to access application from fragment
        val application = requireActivity().application as MyApplication

        binding.monthPicker.initialize{ month ->
            requireActivity().shortToast("Selected: ${month.name}")
            AppPrefs.monthId = month.id
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            AppPrefs.monthIdFlow.collect {
                requireActivity().shortToast("Selected: $it")
                binding.monthPicker.setSelectedMonthId(it)
            }
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val statusBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // Apply padding to the top of the root layout
            binding.root.updatePadding(
                top = statusBarsInsets.top,
                // Optionally handle navigation bar insets
                bottom = navigationBarsInsets.bottom
            )

            // Return the WindowInsets instance so that it can be consumed by other listeners
            windowInsets
        }
    }

}