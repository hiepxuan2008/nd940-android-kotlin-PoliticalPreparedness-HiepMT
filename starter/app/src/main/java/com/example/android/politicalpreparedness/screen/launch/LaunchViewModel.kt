package com.example.android.politicalpreparedness.screen.launch

import android.app.Application
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.base.NavigationCommand

class LaunchViewModel(val app: Application) : BaseViewModel(app) {
    fun onUpcomingElectionsClicked() {
        navigationCommand.value = NavigationCommand.To(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    fun onFindRepresentationsClicked() {
        navigationCommand.value = NavigationCommand.To(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }
}
