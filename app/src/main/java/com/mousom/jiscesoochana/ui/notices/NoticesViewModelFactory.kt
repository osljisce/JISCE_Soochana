package com.mousom.jiscesoochana.ui.notices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mousom.jiscesoochana.repository.BaseRepository

class NoticesViewModelFactory (
    private val repository: BaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoticesViewModel(repository) as T
    }
}