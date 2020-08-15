package com.cartrack.assignment.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class DatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) = db.run {
        beginTransaction()
        try {
            db.execSQL("INSERT INTO account VALUES(1, 'test', 'test1234', 'SG');")

            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }
}