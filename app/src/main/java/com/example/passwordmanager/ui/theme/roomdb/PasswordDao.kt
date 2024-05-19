package com.example.passwordmanager.ui.theme.roomdb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.passwordmanager.ui.theme.model.PasswordModel

@Dao
interface PasswordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordModel)

    @Update
    suspend fun updatePassword(password: PasswordModel)

    @Delete
    suspend fun deletePassword(password: PasswordModel)

    @Query("SELECT * FROM passwords")
    fun getPasswords(): LiveData<MutableList<PasswordModel>>
}