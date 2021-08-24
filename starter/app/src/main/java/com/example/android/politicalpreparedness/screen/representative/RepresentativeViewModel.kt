package com.example.android.politicalpreparedness.screen.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.local.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.screen.representative.model.Representative
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.data.dto.Result

class RepresentativeViewModel(private val repository: ElectionDataSource, val app: Application) :
    BaseViewModel(app) {

    val addressLine1 = MutableLiveData<String>()
    val addressLine2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives


    fun findMyRepresentatives() {
        hideKeyboard.value = true
        showLoading.value = true

        viewModelScope.launch {
            val result = repository.getRepresentatives(getCurrentAddress().toFormattedString())
            showLoading.value = false
            when (result) {
                is Result.Success -> {
                    _representatives.value = result.data.offices.flatMap { office ->
                        office.getRepresentatives(result.data.officials)
                    }
                }
                is Result.Error -> {
                    _representatives.value = emptyList()
                }
            }
        }
    }

    private fun getCurrentAddress() = Address(
        addressLine1.value.toString(),
        addressLine2.value.toString(),
        city.value.toString(),
        state.value.toString(),
        zip.value.toString()
    )

    fun updateState(newState: String) {
        state.value = newState
    }

    fun updateAddress(address: Address) {
        addressLine1.value = address.line1
        addressLine2.value = address.line2 ?: ""
        city.value = address.city
        state.value = address.state
        zip.value = address.zip
    }
}
