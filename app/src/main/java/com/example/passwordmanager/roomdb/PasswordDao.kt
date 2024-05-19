package com.example.passwordmanager.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.passwordmanager.model.PasswordModel

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