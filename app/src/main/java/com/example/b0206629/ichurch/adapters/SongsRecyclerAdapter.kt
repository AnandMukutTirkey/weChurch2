package com.example.b0206629.ichurch.adapters

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.models.Song
import kotlinx.android.synthetic.main.item_list.view.*



class SongsRecyclerAdapter(private val songs : ArrayList<Song>, val clickListener : (Song) -> Unit): RecyclerView.Adapter<SongsRecyclerAdapter.SongHolder>(), Filterable {

    var filteredSongs = ArrayList<Song>(0)


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SongHolder {
        val inflateView = p0.inflate(R.layout.item_list)
        filteredSongs.clear()
        for (song in songs) {
            filteredSongs.add(Song(song.number, song.title, song.song))
        }
        return SongHolder(inflateView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(p0: SongHolder, p1: Int) {
        val song = songs[p1]
        p0.bindSong(song, clickListener)
    }

    override fun getFilter(): Filter {
        return filterr
    }

    private val filterr = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Song>(0)
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(filteredSongs)
            } else {
                //Log.d("constraint is ", constraint.toString())
                //Log.d("size of array is ", filteredSongs.size.toString())
                val filterPattern = constraint.toString().toLowerCase().trim()
                for (item in filteredSongs) {
                    if (item.number.toString().trim().contains(filterPattern) || item.title.toLowerCase().trim().contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            var filterResults = FilterResults()
            filterResults.values = filteredList
            Log.d("filterResult size ", filteredList.size.toString())
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            //Log.d("recieved ", results.values.toString())
            songs.clear()
            //Log.d("before publishing", (results.values as ArrayList<Song>).size.toString())
            songs.addAll(results.values as ArrayList<Song>)
            //Log.d("publishing ", songs.size.toString())
            notifyDataSetChanged()
        }
    }

    class SongHolder(v:View) : RecyclerView.ViewHolder(v){
        var view : View = v
        fun bindSong(song: Song, clickListener: (Song) -> Unit){
            var title = song.title
            if(song.number != null) title = song.number.toString().plus(". ").plus(title)
            view.text.text = title
            view.subtext.text = song.song
            view.subtext.setSingleLine(true)
            view.date.text = null
            view.delete.visibility = View.GONE
            view.setOnClickListener { clickListener(song) }
        }
    }

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attatchToRoot : Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attatchToRoot)
    }
}