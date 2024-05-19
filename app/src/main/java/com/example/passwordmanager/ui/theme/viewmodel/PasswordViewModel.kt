package com.example.passwordmanager.ui.theme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.ui.theme.PasswordRepository
import com.example.passwordmanager.ui.theme.model.PasswordModel
import kotlinx.coroutines.launch

class PasswordViewModel(val repository: PasswordRepository) : ViewModel() {
    val passwords: LiveData<MutableList<PasswordModel>> = repository.passwords

    fun insertPassword(password: PasswordModel) : Boolean{
        try {
            viewModelScope.launch {
                repository.insertPassword(password)
            }
            return true
        } catch (e : Exception){
            return false
        }

    }

    fun updatePassword(password: PasswordModel) : Boolean{
        try {
            viewModelScope.launch {
                repository.updatePassword(password)
            }
            return true
        }
        catch (e : Exception){
            return false
        }

    }
    fun deletePassword(password: PasswordModel) : Boolean{
        try {
            viewModelScope.launch {
                repository.deletePassword(password)
            }
            return true
        }catch (e : Exception){
            return false
        }

    }

}
