package com.yourcompany.incometax

import android.app.Application
import com.yourcompany.incometax.data.database.TaxDatabase

class TaxCalculatorApp : Application() {

    lateinit var database: TaxDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = TaxDatabase.getDatabase(this)
    }

    companion object {
        lateinit var instance: TaxCalculatorApp
            private set

        fun getAppDatabase(): TaxDatabase {
            return instance.database
        }
    }
}
