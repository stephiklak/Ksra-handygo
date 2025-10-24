package com.ksra_handygo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class MainViewModel : ViewModel() {

    private val _userResponse = MutableLiveData<String>()
    val userResponse: LiveData<String> get() = _userResponse

    fun fetchUsers(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://98.81.162.163:8443/api/users")
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: "No response body"
                _userResponse.postValue(body)
            } catch (e: Exception) {
                _userResponse.postValue("Error: ${e.message}")
            }
        }
    }
}
