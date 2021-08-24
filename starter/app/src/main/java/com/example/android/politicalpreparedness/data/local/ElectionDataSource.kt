package com.example.android.politicalpreparedness.data.local

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.dto.VoterInfoDTO
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.data.dto.Result

interface ElectionDataSource {

    suspend fun getUpcomingElections(): Result<ElectionResponse>

    fun getSavedElections(): LiveData<List<Election>>

    suspend fun getElection(id: Long): Election?

    suspend fun insert(election: Election)

    suspend fun delete(election: Election)

    suspend fun getVoterInfo(address: String, electionId: Long): Result<VoterInfoDTO>

    suspend fun getRepresentatives(address: String): Result<RepresentativeResponse>

}