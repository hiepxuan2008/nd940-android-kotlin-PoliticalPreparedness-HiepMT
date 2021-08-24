package com.example.android.politicalpreparedness.screen.election

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VoterInfoFragment : BaseFragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private val args: VoterInfoFragmentArgs by navArgs()
    override val viewModel: VoterInfoViewModel by viewModel {
        parametersOf(args.argElection)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_voter_info,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        viewModel.voterInfo.observe(viewLifecycleOwner, Observer { voteInfo ->
            setMessageWithClickableLink(binding.tvVotingLocations,
                getString(R.string.voting_locations_with_url),
                voteInfo.votingLocationFinderUrl
            )
            setMessageWithClickableLink(binding.tvBallotInformation,
                getString(R.string.ballot_information_with_url),
                voteInfo.ballotInfoUrl
            )
        })
    }

    private fun setMessageWithClickableLink(textView: TextView, pattern: String, url: String) {
        //Clickable Span will help us to make clickable a text
        val clickableSpan = object: ClickableSpan() {
            override fun onClick(textView: View) {
                //To open the url in a browser
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        }
        val startIndex = pattern.indexOf("{url}")
        val endIndex = startIndex + url.length

        val content = pattern.replace("{url}", url)
        //SpannableString will be created with the full content and
        // the clickable content all together
        val spannableString = SpannableString(content)
        //only the word 'link' is clickable
        spannableString.setSpan(clickableSpan, startIndex, endIndex,      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //The following is to set the new text in the TextView
        //no styles for an already clicked link
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }
}