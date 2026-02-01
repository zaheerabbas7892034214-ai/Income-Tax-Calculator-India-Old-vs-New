package com.yourcompany.incometax.domain.repository

import com.google.gson.Gson
import com.yourcompany.incometax.data.dao.ProfileDao
import com.yourcompany.incometax.data.entity.ProfileEntity
import com.yourcompany.incometax.data.model.TaxInputs
import kotlinx.coroutines.flow.Flow

class ProfileRepository(
    private val profileDao: ProfileDao
) {
    private val gson = Gson()
    
    fun getAllProfiles(): Flow<List<ProfileEntity>> {
        return profileDao.getAllProfiles()
    }
    
    suspend fun saveProfile(name: String, inputs: TaxInputs) {
        val inputsJson = gson.toJson(inputs)
        val profile = ProfileEntity(
            name = name,
            inputsJson = inputsJson
        )
        profileDao.insertProfile(profile)
    }
    
    suspend fun deleteProfile(id: Long) {
        profileDao.deleteProfile(id)
    }
    
    suspend fun deleteAllProfiles() {
        profileDao.deleteAllProfiles()
    }
    
    fun deserializeInputs(json: String): TaxInputs {
        return try {
            gson.fromJson(json, TaxInputs::class.java)
        } catch (e: Exception) {
            TaxInputs()
        }
    }
}
