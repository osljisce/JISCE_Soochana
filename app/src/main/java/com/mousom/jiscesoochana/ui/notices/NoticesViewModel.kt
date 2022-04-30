package com.mousom.jiscesoochana.ui.notices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mousom.jiscesoochana.repository.BaseRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class NoticesViewModel(
    private val repository: BaseRepository,
) : ViewModel() {

    val getNoticeDataResponse: MutableLiveData<Response<String>> = MutableLiveData()

    fun getNoticeData(url: String) {
        viewModelScope.launch {
            val response = repository.getNoticeData(url)
            getNoticeDataResponse.value = response
        }
    }

}