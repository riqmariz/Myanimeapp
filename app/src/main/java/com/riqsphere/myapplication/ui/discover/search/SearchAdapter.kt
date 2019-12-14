package com.riqsphere.myapplication.ui.discover.search

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.riqsphere.myapplication.R
import com.riqsphere.myapplication.model.search.SearchModel
import com.riqsphere.myapplication.model.watchlist.WatchlistAnime
import com.riqsphere.myapplication.utils.ImageHandler

class SearchAdapter(private val act: Activity) : BaseAdapter() {
    private var watchlist: List<WatchlistAnime> = arrayListOf()
    private var searchResults = ArrayList<SearchModel>()

    fun setData(list: ArrayList<SearchModel>){
        searchResults = list
        notifyDataSetChanged()
    }

    fun setWatchlistData(list: List<WatchlistAnime>) {
        watchlist = list
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: act.layoutInflater.inflate(R.layout.search_listview, parent,false)

        val sm = searchResults[position]

        val searchAnimeTitle:TextView = view.findViewById(R.id.lv_anime_title)
        val searchImage:ImageView = view.findViewById(R.id.lv_image)
        val searchScore:TextView = view.findViewById(R.id.lv_score)
        val searchAdded:ImageView = view.findViewById(R.id.lv_add_to_list)
        val searchScoreStar: ImageView = view.findViewById(R.id.lv_star)
        val searchScoreBg: ImageView = view.findViewById(R.id.lv_rectangle_score)

        searchAnimeTitle.text = sm.animeTitle
        ImageHandler.getInstance(this@SearchAdapter.act).load(sm.imageURL).into(searchImage)
        searchScore.text = sm.score

        val resource = if (watchlist.any { it.id == sm.id }) {
            R.drawable.ic_added_to_list
        } else {
            R.drawable.ic_add_to_list
        }
        searchAdded.setImageResource(resource)

        searchScoreStar.setImageResource(R.drawable.ic_star_1)
        searchScoreBg.setImageResource(R.drawable.rectangle_score)

        return view
    }

    override fun getItem(position: Int): Any {
        return searchResults[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return searchResults.size
    }

}