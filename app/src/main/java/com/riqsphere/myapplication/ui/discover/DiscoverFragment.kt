package com.riqsphere.myapplication.ui.animes

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.riqsphere.myapplication.R
import com.riqsphere.myapplication.cache.JikanCacheHandler
import com.riqsphere.myapplication.model.search.SearchModel
import com.riqsphere.myapplication.model.watchlist.room.WatchlistAnime
import com.riqsphere.myapplication.model.watchlist.room.WatchlistViewModel
import com.riqsphere.myapplication.ui.discover.DiscoverAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class DiscoverFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var viewPool: RecyclerView.RecycledViewPool
    private lateinit var allWatchlistAnime: LiveData<List<WatchlistAnime>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView =  inflater.inflate(R.layout.fragment_discover, container, false)
        if (activity == null) {
            return rootView
        }

        viewPool = RecyclerView.RecycledViewPool()
        allWatchlistAnime = WatchlistViewModel(activity!!.application).allWatchlistAnime

        val empty = { arrayListOf (
            SearchModel(JikanCacheHandler.getAnime(27)),
            SearchModel(JikanCacheHandler.getAnime(20)),
            SearchModel(JikanCacheHandler.getAnime(21)),
            SearchModel(JikanCacheHandler.getAnime(22)),
            SearchModel(JikanCacheHandler.getAnime(23)),
            SearchModel(JikanCacheHandler.getAnime(24))
        )}

        val top4uRecyclerView = initializeRecyclerView(R.id.dc_rv_top4u)
        val top4uPair = Pair(top4uRecyclerView, empty)

        val seasonalRecyclerView = initializeRecyclerView(R.id.dc_rv_seasonal)
        val seasonalPair = Pair(seasonalRecyclerView, fetchCurrentSeason)

        val topUpcomingRecyclerView = initializeRecyclerView(R.id.dc_rv_top_upcoming)
        val topUpcomingPair = Pair(topUpcomingRecyclerView, fetchTopUpcoming)

        val mostPoplarRecyclerView = initializeRecyclerView(R.id.dc_rv_mp)
        val mostPoplarPair = Pair(mostPoplarRecyclerView, fetchMostPoplar)

        val topScoreRecyclerView = initializeRecyclerView(R.id.dc_rv_score)
        val topScorePair = Pair(topScoreRecyclerView, fetchTopScore)

        val pairs = arrayOf(top4uPair, seasonalPair, topUpcomingPair, mostPoplarPair, topScorePair)
        AsyncDataSetter().execute(*pairs)

        val recyclers = arrayOf(top4uRecyclerView, seasonalRecyclerView, topUpcomingRecyclerView, mostPoplarRecyclerView, topScoreRecyclerView)
        observe(*recyclers)

        return rootView
    }

    private val fetchCurrentSeason = {
        SearchModel.arrayListFromSeasonSearch(JikanCacheHandler.getCurrentSeason())
    }

    private val fetchTopUpcoming = {
        SearchModel.arrayListFromTopListing(JikanCacheHandler.getTopUpcoming())
    }

    private val fetchMostPoplar = {
        SearchModel.arrayListFromAnimePageAnime(JikanCacheHandler.getMostPoplar())
    }

    private val fetchTopScore = {
        SearchModel.arrayListFromAnimePageAnime(JikanCacheHandler.getTopScore())
    }

    private fun initializeRecyclerView(recyclerViewID: Int): RecyclerView {
        val adapt = DiscoverAdapter(activity!!)
        val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val rv = rootView.findViewById<RecyclerView>(recyclerViewID)
        rv.apply {
            layoutManager = manager
            adapter = adapt
            setRecycledViewPool(viewPool)
        }
        return rv
    }

    private fun observe(vararg params: RecyclerView) {
        params.forEach { recyclerView ->
            allWatchlistAnime.observe(this, Observer {
                if (recyclerView.adapter is DiscoverAdapter) {
                    val adapt = recyclerView.adapter as DiscoverAdapter
                    adapt.setWatchlistData(it)
                }
            })
        }
    }

    private class AsyncDataSetter: AsyncTask<Pair<RecyclerView, () -> ArrayList<SearchModel>>, Void, Void>() {
        override fun doInBackground(vararg params: Pair<RecyclerView, () -> ArrayList<SearchModel>>?): Void? {
            runBlocking {
                params.forEach { pair ->
                    launch {
                        if (pair != null) {
                            val recyclerView = pair.first
                            val data = fetchData(pair.second)
                            recyclerView.setData(data)
                        }
                    }
                }
            }
            return null
        }

        private inline fun <T> fetchData(func: () -> T) = func()

        private fun RecyclerView.setData(data: ArrayList<SearchModel>) {
            if (adapter is DiscoverAdapter) {
                val discoverAdapter = adapter as DiscoverAdapter
                MainScope().launch {
                    discoverAdapter.setSearchData(data)
                }
            }
        }
    }
}