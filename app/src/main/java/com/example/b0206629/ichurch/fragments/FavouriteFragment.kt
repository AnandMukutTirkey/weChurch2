package com.example.b0206629.ichurch.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.adapters.FavouriteRecyclerAdapter
import com.example.b0206629.ichurch.models.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavouriteFragment : Fragment() {

    private var faourites : ArrayList<Song> = ArrayList(0)

    companion object {
        fun newInstance() = FavouriteFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        faourites = getFavourites()
        if (faourites.isEmpty()){
            faourites.add(Song(-1, "liked songs appear here", "all the songs liked ❤️ by you appear here "))
        }
        favourite.layoutManager = LinearLayoutManager(activity)
        favourite.adapter = FavouriteRecyclerAdapter(faourites, {song -> favouriteClicked(song) }, { i ->  deleteFavourite(i)})
    }

    override fun onPause() {
        super.onPause()
        var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
        editor.putString("lastFragment", "FavouriteFragment").apply()
    }

    private fun favouriteClicked(favourite : Song){
        fragmentManager!!.beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.frame_container,SongFragment.newInstance(favourite.title, favourite.song, false)).addToBackStack(null).commit()
    }

    private fun deleteFavourite(index : Int){
        Log.d("FavouriteFragment", "deleteFavourite")
        var alert = AlertDialog.Builder(activity).create()
        alert.setTitle("Warning")
        alert.setMessage("Are you sure to remove it from favourite list")
        alert.setButton(AlertDialog.BUTTON_POSITIVE,"Yes"){ dialog, which ->
            faourites.removeAt(index)
            var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
            editor.putString("favourites", Gson().toJson(faourites)).apply()
            favourite.adapter!!.notifyDataSetChanged()
        }
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){ dialog, which -> }
        alert.show()
    }

    private fun getFavourites(): ArrayList<Song>{
        var favourites = ArrayList<Song>(0)
        var favouritesJson = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).getString("favourites",null) ?: return favourites
        favourites.addAll(Gson().fromJson(favouritesJson, object : TypeToken<List<Song>>() {}.type))
        return favourites
    }

}
