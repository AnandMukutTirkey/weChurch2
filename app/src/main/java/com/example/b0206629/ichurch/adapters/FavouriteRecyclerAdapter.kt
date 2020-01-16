package com.example.b0206629.ichurch.adapters

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.models.Song
import kotlinx.android.synthetic.main.item_list.view.*

class FavouriteRecyclerAdapter(private val favorites : ArrayList<Song>, val clickListener : (Song) -> Unit, val delete : (Int) -> Unit)
    : RecyclerView.Adapter<FavouriteRecyclerAdapter.SongHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SongHolder {
        val inflateView = p0.inflate(R.layout.item_list)
        return SongHolder(inflateView)
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    override fun onBindViewHolder(p0: SongHolder, p1: Int) {
        val song = favorites[p1]
        p0.bindSong(song, p1, clickListener, delete)
    }

    class SongHolder(v: View) : RecyclerView.ViewHolder(v){
        var view : View = v
        fun bindSong(song: Song, index: Int, clickListener: (Song) -> Unit, delete: (Int) -> Unit){
            view.text.text = song.title
            view.subtext.text = song.song
            view.subtext.setSingleLine(true)
            view.date.text = null
            if (song.number == -1) view.delete.visibility = View.GONE
            else view.delete.visibility = View.VISIBLE
            view.delete.setOnClickListener { delete(index) }
            view.setOnClickListener { clickListener(song) }
        }
    }
    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attatchToRoot : Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attatchToRoot)
    }
}