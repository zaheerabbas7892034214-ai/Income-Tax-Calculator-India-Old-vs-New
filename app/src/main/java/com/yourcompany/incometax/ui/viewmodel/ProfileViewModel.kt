package com.yourcompany.incometax.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.incometax.data.entity.ProfileEntity
import com.yourcompany.incometax.data.model.TaxInputs
import com.yourcompany.incometax.domain.repository.EntitlementRepository
import com.yourcompany.incometax.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val entitlementRepository: EntitlementRepository
) : ViewModel() {
    
    val profiles: StateFlow<List<ProfileEntity>> = profileRepository.getAllProfiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val isPro: StateFlow<Boolean> = entitlementRepository.isPro()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    fun saveProfile(name: String, inputs: TaxInputs) {
        viewModelScope.launch {
            profileRepository.saveProfile(name, inputs)
        }
    }
    
    fun deleteProfile(id: Long) {
        viewModelScope.launch {
            profileRepository.deleteProfile(id)
        }
    }
    
    fun loadProfile(profile: ProfileEntity): TaxInputs {
        return profileRepository.deserializeInputs(profile.inputsJson)
    }
}
