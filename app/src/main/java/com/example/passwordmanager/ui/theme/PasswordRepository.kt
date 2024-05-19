package com.example.passwordmanager.ui.theme

import androidx.lifecycle.LiveData
import com.example.passwordmanager.ui.theme.model.PasswordModel
import com.example.passwordmanager.ui.theme.roomdb.PasswordDao
import com.example.passwordmanager.ui.theme.utils.EncryptionHelper

class PasswordRepository(private val passwordDao: PasswordDao) {
    //val passwords: Flow<List<PasswordModel>> = passwordDao.getPasswords()
    val passwords: LiveData<MutableList<PasswordModel>> = passwordDao.getPasswords()
    suspend fun insertPassword(password: PasswordModel)  {
        val encryptedPassword = EncryptionHelper.encrypt( password.password)
        val encryptedPasswordModel = password.copy(password = encryptedPassword)
        passwordDao.insertPassword(encryptedPasswordModel)
    }

    suspend fun updatePassword(password: PasswordModel)  {
        val encryptedPassword = EncryptionHelper.encrypt( password.password)
        val encryptedPasswordModel = password.copy(password = encryptedPassword)
        passwordDao.updatePassword(encryptedPasswordModel)
    }


    suspend fun deletePassword(password: PasswordModel) {
        passwordDao.deletePassword(password)
    }



}
