package com.example.passwordmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val accountName: String,val userName : String, val password : String)