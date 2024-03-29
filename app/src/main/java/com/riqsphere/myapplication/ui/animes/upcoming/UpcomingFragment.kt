package com.riqsphere.myapplication.ui.animes.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.jikan4java.types.main.schedule.Schedule
import com.github.doomsdayrs.jikan4java.types.main.schedule.SubAnime
import com.riqsphere.myapplication.R
import com.riqsphere.myapplication.cache.JikanCacheHandler
import com.riqsphere.myapplication.model.watchlist.WatchlistAnime
import com.riqsphere.myapplication.room.MyaaViewModel
import com.riqsphere.myapplication.utils.addWeekday
import com.riqsphere.myapplication.utils.nthWeekday
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class UpcomingFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewAdapter: UpcomingAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var myaaViewModel: MyaaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myaaViewModel =
            MyaaViewModel(this.activity!!.application)
        myaaViewModel.allWatchlistAnime.observe(this, Observer {
            it?.let {
                GlobalScope.launch {
                    val upcoming = fetchUpcoming(it)
                    MainScope().launch {
                        viewAdapter.setData(upcoming)
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }
        })

        val view = inflater.inflate(R.layout.fragment_upcoming, container, false)
        val upcomingAdapter =
            UpcomingAdapter(activity!!)

        viewManager = LinearLayoutManager(activity)
        viewAdapter = upcomingAdapter
        recyclerView = view.findViewById(R.id.uc_rv)
        progressBar = view.findViewById(R.id.uc_pb)
        recyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter

        }

        return view
    }

    private fun fetchUpcoming(allWatchlistAnime: List<WatchlistAnime>): ArrayList<WatchlistAnime> {
        val schedule = JikanCacheHandler.getCurrentSchedule()
        return filteredUpcoming(schedule, allWatchlistAnime)
    }


    private fun filteredUpcoming(schedule: Schedule, watchlistAnimes: List<WatchlistAnime>): ArrayList<WatchlistAnime> {
        val nestedUpcoming = nestedAndOrderedUpcoming(schedule)
        return spreadAndFilter(nestedUpcoming, watchlistAnimes)
    }

    private fun nestedAndOrderedUpcoming(schedule: Schedule): ArrayList<ArrayList<SubAnime>> {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val result: ArrayList<ArrayList<SubAnime>> = arrayListOf()
        for (x in 0..6) {
            val weekday = addWeekday(today, x)
            result.add(schedule.nthWeekday(weekday))
        }
        return result
    }

    private fun spreadAndFilter (nestedSchedule: ArrayList<ArrayList<SubAnime>>, watchlistAnimes: List<WatchlistAnime>): ArrayList<WatchlistAnime> {
        //spread the arrays held within nestedUpcoming
        return nestedSchedule.fold(
            initial = ArrayList(),
            operation = { acc, arrayList ->
                acc.addAll(
                    arrayList.mapNotNull {
                        //filter to accept only animes present in both the watchlist and the schedule
                        watchlistAnimes.firstOrNull { wa -> wa.id == it.mal_id }
                    }
                )
                acc
            }
        )
    }
}
