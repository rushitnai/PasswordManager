package com.example.passwordmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.PasswordRepository
import com.example.passwordmanager.model.PasswordModel
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
