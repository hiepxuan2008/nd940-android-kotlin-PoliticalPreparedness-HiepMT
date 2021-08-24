package com.example.android.politicalpreparedness.screen.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.dto.Result
import com.example.android.politicalpreparedness.data.dto.VoterInfoDTO
import com.example.android.politicalpreparedness.data.local.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val repository: ElectionDataSource, val election: Election, val app: Application) : BaseViewModel(app) {

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    private var _isFollow = MutableLiveData<Boolean>()
    val isFollow: LiveData<Boolean>
        get() = _isFollow

    private var _voterInfo = MutableLiveData<VoterInfoDTO>()
    val voterInfo: LiveData<VoterInfoDTO>
        get() = _voterInfo

    init {
        checkIsFollowing()
        fetchVoterInfo()
    }

    private fun checkIsFollowing() {
        viewModelScope.launch {
            _isFollow.value = repository.getElection(election.id) != null
        }
    }

    private fun fetchVoterInfo() {
        viewModelScope.launch {
            if (election.division.state.isNotEmpty()) {
                val address = "${election.division.country},${election.division.state}"
                val result = repository.getVoterInfo(address, election.id)
                when (result) {
                    is Result.Success -> {
                        _voterInfo.value = result.data
                    }
                    is Result.Error -> {
                        _voterInfo.value = VoterInfoDTO()
                        showToast.value = app.getString(R.string.error_voter_information)
                    }
                }
            }
        }
    }

    fun toggleElection(election: Election) {
        viewModelScope.launch {
            if (isFollow.value == true) {
                repository.delete(election)
            } else {
                repository.insert(election)
            }
            checkIsFollowing()
        }
    }

    fun onURLClick(url: String) {
        openUrl.value = url
    }
}