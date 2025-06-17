package com.logicline.mydining.ui.fragments.menus

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.logicline.mydining.R
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.User
import com.logicline.mydining.data.models.response.ProfileResponse
import com.logicline.mydining.databinding.FragmentProfileBinding
import com.logicline.mydining.ui.viewmodels.ProfileViewModel
import com.logicline.mydining.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val profileViewModel: ProfileViewModel by viewModels()
    private var currentUser: User? = null
    
    // Image picker contracts
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uploadImage(it) }
    }
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { uploadImage(it) }
        }
    }
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    private var tempImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
        profileViewModel.getProfile()
    }    private fun setupUI() {
        binding.apply {
            // Edit button click
            btnEdit.setOnClickListener {
                toggleEditMode()
            }
            
            // Avatar change
            fabChangeAvatar.setOnClickListener {
                showImagePickerDialog()
            }
            
            // Save button click
            btnSave.setOnClickListener {
                saveProfile()
            }
            
            // Cancel button click
            btnCancel.setOnClickListener {
                toggleEditMode(false)
                loadCurrentProfileData()
            }
            
            // Setup gender dropdown
            setupGenderDropdown()
            
            // Load profile data
            profileViewModel.getProfile()
        }
    }

    private fun observeViewModel() {        viewLifecycleOwner.lifecycleScope.launch {
            // Observe profile state
            profileViewModel.profileState.collect { state ->
                
                when (state) {
                    is DataState.Success -> {
                        state.data?.let { profileResponse ->
                            updateUI(profileResponse)
                        }
                    }
                    is DataState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {            // Observe update profile state
            profileViewModel.updateProfileState.collect { state ->
                when (state) {
                    is DataState.Loading -> {
                        binding.loadingOverlay.visibility = View.VISIBLE
                    }
                    is DataState.Success -> {
                        binding.loadingOverlay.visibility = View.GONE
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        profileViewModel.getProfile() // Refresh profile
                    }
                    is DataState.Error -> {
                        binding.loadingOverlay.visibility = View.GONE
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.loadingOverlay.visibility = View.GONE
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe avatar upload state
            profileViewModel.avatarUploadState.collect { state ->
                when (state) {
                    is DataState.Loading -> {
                        binding.avatarProgressBar.visibility = View.VISIBLE
                    }
                    is DataState.Success -> {
                        binding.avatarProgressBar.visibility = View.GONE
                        Toast.makeText(context, "Avatar uploaded successfully", Toast.LENGTH_SHORT).show()
                        profileViewModel.getProfile() // Refresh profile
                    }
                    is DataState.Error -> {
                        binding.avatarProgressBar.visibility = View.GONE
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.avatarProgressBar.visibility = View.GONE
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe avatar remove state
            profileViewModel.avatarRemoveState.collect { state ->
                when (state) {
                    is DataState.Loading -> {
                        binding.avatarProgressBar.visibility = View.VISIBLE
                    }
                    is DataState.Success -> {
                        binding.avatarProgressBar.visibility = View.GONE
                        Toast.makeText(context, "Avatar removed successfully", Toast.LENGTH_SHORT).show()
                        profileViewModel.getProfile() // Refresh profile
                    }
                    is DataState.Error -> {
                        binding.avatarProgressBar.visibility = View.GONE
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.avatarProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }    private fun updateUI(profileResponse: ProfileResponse) {
        currentUser = profileResponse.user
        
        binding.apply {
            // Header display
            tvDisplayName.text = profileResponse.user.name
            tvDisplayEmail.text = profileResponse.user.email
            
            // View mode information
            tvName.text = profileResponse.user.name
            tvEmail.text = profileResponse.user.email
            tvPhone.text = profileResponse.user.phone ?: "Not provided"
            tvGender.text = profileResponse.user.gender?.replaceFirstChar { it.uppercase() } ?: "Not specified"
            tvAddress.text = profileResponse.user.city ?: "Not provided"
            
            // Load current data into edit fields
            loadCurrentProfileData()
            
            // Load avatar
            if (profileResponse.user.photoUrl.isNullOrEmpty()) {
                profileImage.setImageResource(R.drawable.man)
            } else {
                Glide.with(this@ProfileFragment)
                    .load(profileResponse.user.photoUrl)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.man)
                    .error(R.drawable.man)
                    .into(profileImage)
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }    private fun openCamera() {
        // Create a temporary image file
        val tempFile = File(requireContext().cacheDir, "temp_camera_image.jpg")
        tempImageUri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            tempFile
        )
        tempImageUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun uploadImage(uri: Uri) {
        val tempFile = FileUtils.createTempFileFromUri(requireContext(), uri, "profile_image")
        tempFile?.let {
            profileViewModel.uploadAvatar(it)
        } ?: run {
            Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
        }
    }    private fun showRemoveAvatarDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Avatar")
            .setMessage("Are you sure you want to remove your profile picture?")
            .setPositiveButton("Remove") { _, _ ->
                profileViewModel.removeAvatar()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupGenderDropdown() {
        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            genderOptions
        )
        binding.actvGender.setAdapter(adapter)
    }
    
    private fun toggleEditMode(editMode: Boolean = true) {
        binding.apply {
            if (editMode) {
                layoutViewMode.visibility = View.GONE
                layoutEditMode.visibility = View.VISIBLE
                btnEdit.text = "Cancel"
            } else {
                layoutViewMode.visibility = View.VISIBLE
                layoutEditMode.visibility = View.GONE
                btnEdit.text = "Edit"
            }
        }
    }
      private fun saveProfile() {
        binding.apply {
            val name = etName.text.toString().trim()
            val gender = actvGender.text.toString().trim()
            val city = etAddress.text.toString().trim()
            
            if (name.isEmpty()) {
                etName.error = "Name is required"
                return
            }
            
            // Call ViewModel to update profile (only name, city, gender)
            profileViewModel.updateProfile(name, city, gender)
            toggleEditMode(false)
        }
    }
    
    private fun loadCurrentProfileData() {
        currentUser?.let { user ->
            binding.apply {
                etName.setText(user.name)
                etEmail.setText(user.email)
                etPhone.setText(user.phone)
                actvGender.setText(user.gender?.replaceFirstChar { it.uppercase() }, false)
                etAddress.setText(user.city ?: "")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        profileViewModel.resetStates()
    }
}
