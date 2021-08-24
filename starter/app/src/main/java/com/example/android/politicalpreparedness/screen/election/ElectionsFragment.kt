package com.example.android.politicalpreparedness.screen.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.screen.election.adapter.ElectionListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class ElectionsFragment : BaseFragment() {

    override val viewModel: ElectionsViewModel by viewModel()

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_election,
            container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        val electionsAdapter = ElectionListAdapter(ElectionListAdapter.ClickListener {
            viewModel.onClickElectionItem(it)
        })
        binding.rvUpcomingElection.adapter = electionsAdapter
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer { elections ->
            electionsAdapter.submitList(elections)
        })

        val savedElectionsAdapter = ElectionListAdapter(ElectionListAdapter.ClickListener {
            viewModel.onClickElectionItem(it)
        })
        binding.rvSavedElection.adapter = savedElectionsAdapter
        viewModel.savedElections.observe(viewLifecycleOwner, Observer { elections ->
            savedElectionsAdapter.submitList(elections)
        })
    }
}