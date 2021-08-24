package com.example.android.politicalpreparedness.di

import com.example.android.politicalpreparedness.data.local.ElectionDataSource
import com.example.android.politicalpreparedness.data.local.ElectionRepository
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.screen.election.ElectionsViewModel
import com.example.android.politicalpreparedness.screen.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.screen.launch.LaunchViewModel
import com.example.android.politicalpreparedness.screen.representative.RepresentativeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { ElectionDatabase.getInstance(get()).electionDao }
}

val apiModule = module {
    single {
        return@single CivicsApi.createRetrofitService()
    }
}

val repositoryModule = module {
    single { ElectionRepository(get(), get()) as ElectionDataSource }
}

val launchModule = module {
    viewModel { LaunchViewModel(get()) }
}

val electionModule = module {
    viewModel { ElectionsViewModel(get(), get()) }
}

val voterInfoModule = module {
    viewModel { (election: Election) -> VoterInfoViewModel(get(), election, get()) }
}

val representativeModule = module {
    viewModel { RepresentativeViewModel(get(), get()) }
}

