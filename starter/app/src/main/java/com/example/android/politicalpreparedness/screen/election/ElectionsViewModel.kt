package com.example.android.politicalpreparedness.screen.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.base.NavigationCommand
import com.example.android.politicalpreparedness.data.dto.Result
import com.example.android.politicalpreparedness.data.local.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(private val repository: ElectionDataSource, private val app: Application) :
    BaseViewModel(app) {

    // Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    // Create live data val for saved elections
    val savedElections = repository.getSavedElections()

    init {
        fetchUpcomingElections()
    }

    private fun fetchUpcomingElections() {
        showLoading.value = true
        viewModelScope.launch {
            val result = repository.getUpcomingElections()
            showLoading.value = false
            when (result) {
                is Result.Success -> {
                    _upcomingElections.value = result.data.elections
                }
                is Result.Error -> {
                    _upcomingElections.value = emptyList()
                    showToast.value = app.getString(R.string.error_upcoming_election)
                }
            }
        }
    }

    fun onClickElectionItem(election: Election) {
        navigationCommand.value = NavigationCommand.To(
            ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                election
            )
        )
    }
}