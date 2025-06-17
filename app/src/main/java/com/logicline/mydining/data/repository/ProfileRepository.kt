package com.logicline.mydining.data.repository

import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.User
import com.logicline.mydining.data.models.response.AvatarUploadResponse
import com.logicline.mydining.data.models.response.ProfileResponse
import com.logicline.mydining.data.models.response.ProfileUpdateRequest
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.network.MyApi
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val api: MyApi
) {
    suspend fun getProfile(): DataState<ProfileResponse?> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body()?.error == false) {
                DataState.Success(response.body()?.data)
            } else {
                DataState.Error(message = response.body()?.msg ?: "Failed to get profile")
            }
        } catch (e: Exception) {
            DataState.Error(message = e.message ?: "Network error")
        }
    }

    suspend fun updateProfile(profileUpdate: ProfileUpdateRequest): DataState<User?> {
        return try {
            val response = api.updateProfile(profileUpdate)
            if (response.isSuccessful && response.body()?.error == false) {
                DataState.Success(response.body()?.data)
            } else {
                DataState.Error(message = response.body()?.msg ?: "Failed to update profile")
            }
        } catch (e: Exception) {
            DataState.Error(message = e.message ?: "Network error")
        }
    }

    suspend fun uploadAvatar(avatar: MultipartBody.Part): DataState<AvatarUploadResponse?> {
        return try {
            val response = api.uploadAvatar(avatar)
            if (response.isSuccessful && response.body()?.error == false) {
                DataState.Success(response.body()?.data)
            } else {
                DataState.Error(message = response.body()?.msg ?: "Failed to upload avatar")
            }
        } catch (e: Exception) {
            DataState.Error(message = e.message ?: "Network error")
        }
    }

    suspend fun removeAvatar(): DataState<Boolean> {
        return try {
            val response = api.removeAvatar()
            if (response.isSuccessful && response.body()?.error == false) {
                DataState.Success(true)
            } else {
                DataState.Error(message = response.body()?.msg ?: "Failed to remove avatar")
            }
        } catch (e: Exception) {
            DataState.Error(message = e.message ?: "Network error")
        }
    }
}
