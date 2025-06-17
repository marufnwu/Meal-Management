package com.logicline.mydining.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.User
import com.logicline.mydining.data.models.response.AvatarUploadResponse
import com.logicline.mydining.data.models.response.ProfileResponse
import com.logicline.mydining.data.models.response.ProfileUpdateRequest
import com.logicline.mydining.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<DataState<ProfileResponse?>>(DataState.Idle())
    val profileState: StateFlow<DataState<ProfileResponse?>> = _profileState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<DataState<User?>>(DataState.Idle())
    val updateProfileState: StateFlow<DataState<User?>> = _updateProfileState.asStateFlow()

    private val _avatarUploadState = MutableStateFlow<DataState<AvatarUploadResponse?>>(DataState.Idle())
    val avatarUploadState: StateFlow<DataState<AvatarUploadResponse?>> = _avatarUploadState.asStateFlow()

    private val _avatarRemoveState = MutableStateFlow<DataState<Boolean>>(DataState.Idle())
    val avatarRemoveState: StateFlow<DataState<Boolean>> = _avatarRemoveState.asStateFlow()

    fun getProfile() {
        viewModelScope.launch {
            _profileState.value = DataState.Loading()
            _profileState.value = profileRepository.getProfile()
        }
    }

    fun updateProfile(name: String? = null, city: String? = null, gender: String? = null) {
        viewModelScope.launch {
            _updateProfileState.value = DataState.Loading()
            val request = ProfileUpdateRequest(name = name, city = city, gender = gender)
            _updateProfileState.value = profileRepository.updateProfile(request)
        }
    }

    fun uploadAvatar(imageFile: File) {
        viewModelScope.launch {
            _avatarUploadState.value = DataState.Loading()
            
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val avatar = MultipartBody.Part.createFormData("avatar", imageFile.name, requestFile)
            
            _avatarUploadState.value = profileRepository.uploadAvatar(avatar)
        }
    }

    fun removeAvatar() {
        viewModelScope.launch {
            _avatarRemoveState.value = DataState.Loading()
            _avatarRemoveState.value = profileRepository.removeAvatar()
        }
    }

    fun resetStates() {
        _updateProfileState.value = DataState.Idle()
        _avatarUploadState.value = DataState.Idle()
        _avatarRemoveState.value = DataState.Idle()
    }
}
