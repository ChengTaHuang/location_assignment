package com.cartrack.assignment.data.local

import androidx.room.Dao
import androidx.room.Query
import com.cartrack.assignment.data.bean.Account
import io.reactivex.Single

@Dao
interface AccountDao {

    @Query("SELECT * FROM account WHERE user_name = :name and password = :password and country_code = :countryCode  LIMIT 1")
    fun getAccount(name: String, password: String, countryCode: String): Single<Account>
}