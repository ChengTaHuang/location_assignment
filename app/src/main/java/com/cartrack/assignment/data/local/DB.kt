package com.cartrack.assignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cartrack.assignment.data.bean.Account

@Database(entities = [Account::class], version = 1)
abstract class DB : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}