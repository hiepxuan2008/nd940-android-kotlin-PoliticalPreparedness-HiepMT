package com.example.android.politicalpreparedness.network.models

import com.example.android.politicalpreparedness.data.dto.VoterInfoDTO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse (
    val election: Election,
    val pollingLocations: String? = null, //TODO: Future Use
    val contests: String? = null, //TODO: Future Use
    val state: List<State>? = null,
    val electionElectionOfficials: List<ElectionOfficial>? = null
)

fun VoterInfoResponse.asVoterInfo(): VoterInfoDTO {
    val electionInfo = this.state?.first()?.electionAdministrationBody
    return VoterInfoDTO(
        name = electionInfo?.name ?: "",
        electionInfoUrl = electionInfo?.electionInfoUrl ?: "",
        votingLocationFinderUrl = electionInfo?.votingLocationFinderUrl ?: "",
        ballotInfoUrl = electionInfo?.ballotInfoUrl ?: "",
        correspondenceAddress = electionInfo?.correspondenceAddress?.toFormattedString() ?: ""
    )
}