package com.salman.bitclock.ui.settings

import androidx.lifecycle.ViewModel
import com.salman.bitclock.utils.SecureStorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val secureStorage: SecureStorageManager
) : ViewModel() {

    private val ACCOUNTABILITY_CONTACT_KEY = "master_accountability_contact"

    private val _accountabilityContact = MutableStateFlow(
        secureStorage.getString(ACCOUNTABILITY_CONTACT_KEY) ?: ""
    )
    val accountabilityContact = _accountabilityContact.asStateFlow()

    fun updateAccountabilityContact(contact: String) {
        _accountabilityContact.value = contact
        secureStorage.saveString(ACCOUNTABILITY_CONTACT_KEY, contact)
    }
}
