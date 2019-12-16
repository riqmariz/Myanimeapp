package com.riqsphere.myapplication.ui.animeDetail.episodes

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.riqsphere.myapplication.R
import com.riqsphere.myapplication.model.animeDetail.episodes.EpisodesModel
import com.riqsphere.myapplication.model.watchlist.WatchlistAnime
import com.riqsphere.myapplication.room.MyaaViewModel
import com.riqsphere.myapplication.utils.onClickListeners.EpisodeWatchedListener
import com.riqsphere.myapplication.utils.ImageHandler


class EpisodesWatchNextAdapter(
    private val context: Context,
    private val myaaViewModel: MyaaViewModel
) : RecyclerView.Adapter<EpisodesWatchNextAdapter.ViewHolder>(){
    private var originalEpList = ArrayList<EpisodesModel>()
    private var epList = ArrayList<EpisodesModel>()
    private var watchedList = SparseBooleanArray()
    private var watchlistAnime: WatchlistAnime? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episodes_watch_next_card,parent,false)
        return ViewHolder(view)
    }

    fun setEpisodes(list: ArrayList<EpisodesModel>) {
        originalEpList = list
        buildEpList()
        notifyDataSetChanged()
    }

    fun setWatchlistAnime(watchlistAnime: WatchlistAnime) {
        this.watchlistAnime = watchlistAnime
        setWatched(watchlistAnime.episodesWatched)
        notifyDataSetChanged()
    }

    private fun setWatched(arr: IntArray) {
        watchedList.clear()
        arr.forEach { watchedList.put(it, true) }
    }

    private fun buildEpList() {
        epList = originalEpList.filterTo(arrayListOf()) { !(watchedList[it.num]) }
    }

    override fun getItemCount(): Int {
        return epList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(position)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val cardEpImage: RoundedImageView = itemView.findViewById(R.id.watch_next_episode_image)
        private val cardEpTitle: TextView = itemView.findViewById(R.id.watch_next_episode_name)
        private val cardEpNum: TextView = itemView.findViewById(R.id.watch_next_episode_num)
        private val cardEpChecked: ImageView = itemView.findViewById(R.id.watch_next_episode_check_as_watched)

        fun bindView(position:Int) {
            val epCard = epList[position]
            if (epCard.imageUrl != "") {
                ImageHandler.getInstance(context).load(epCard.imageUrl).into(cardEpImage)
            }

            cardEpTitle.text = epCard.title
            cardEpNum.text = "Episode " + epCard.num

            val watched = watchedList[epCard.num]
            val resource = if(watched){
                R.drawable.ic_button_checked_as_watched
            } else {
                R.drawable.ic_button_check_as_watched
            }
            cardEpChecked.setImageResource(resource)
            cardEpChecked.setOnClickListener(EpisodeWatchedListener(myaaViewModel, watchlistAnime, epCard.num, watched))
        }
    }
}