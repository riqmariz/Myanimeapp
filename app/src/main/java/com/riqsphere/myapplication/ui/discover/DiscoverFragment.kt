package com.riqsphere.myapplication.ui.animes

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import com.riqsphere.myapplication.R

class DiscoverFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_discover, container, false)
        return view
    }

}