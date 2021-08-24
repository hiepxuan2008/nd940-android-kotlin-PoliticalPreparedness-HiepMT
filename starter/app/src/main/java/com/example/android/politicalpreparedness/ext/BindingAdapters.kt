package com.example.android.politicalpreparedness.ext

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("dateText")
fun TextView.bindElectionDateText(date: Date?) {
    text = if (date == null) {
        ""
    } else {
        val format = SimpleDateFormat("EEEE, MMM. dd, yyyy • HH:mm z", Locale.US)
        format.format(date)
    }
}

@BindingAdapter("followText")
fun Button.bindFollowText(isFollow: Boolean) {
    text = if (isFollow) {
        context.getString(R.string.unfollow_button)
    } else {
        context.getString(R.string.follow_button)
    }
}

@BindingAdapter("visible")
fun View.bindContentVisibility(content: String?) {
    visibility = if (content.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("fadeVisible")
fun View.bindFadeVisible(visible: Boolean? = true) {
    if (tag == null) {
        tag = true
        visibility = if (visible == true) View.VISIBLE else View.GONE
    } else {
        animate().cancel()
        if (visible == true) {
            if (visibility == View.GONE)
                fadeIn()
        } else {
            if (visibility == View.VISIBLE)
                fadeOut()
        }
    }
}
