package com.example.b0206629.ichurch.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast

import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.models.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_song.*
import org.json.JSONArray
import java.io.InputStream
import java.lang.Exception
import kotlin.collections.ArrayList

class SongFragment : Fragment() {
    private var title : String? = null
    private var song: String? = null
    private var menuVisible : Boolean = false
    private var showLikeButton : Boolean = true
    private var isBible : Boolean = false
    private var chapters :  ArrayList<Song> = ArrayList<Song>(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title")
            song = it.getString("song")
            showLikeButton = it.getBoolean("showLikeButton")
            isBible = it.getBoolean("isBible")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String ,song: String, showLikeButton : Boolean = true, isBible: Boolean = false) = SongFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
                putString("song", song)
                putBoolean("showLikeButton", showLikeButton)
                putBoolean("isBible", isBible)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songView.text = song
        if (!showLikeButton) like.hide()
        if (!isBible) {
            seekbar.visibility = View.GONE
            minus.hide()
            plus.hide()
        } else {
            chapters.clear()
            chapters.addAll(getChapters(title!!))
            seekbar.max = chapters.size
        }
        setupActions()
    }

    override fun onPause() {
        super.onPause()
        /*var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
        editor.putString("lastFragment", "HomeFragment").apply()*/
    }

    private fun setupActions(){
        menu.setOnClickListener{
            if (menuVisible){
                menuVisible = false
                fontSizeBrightnessMenu.visibility = View.GONE
                seekBarContainer.visibility = View.GONE
            } else {
                menuVisible = true
                fontSizeBrightnessMenu.visibility = View.VISIBLE
                seekBarContainer.visibility = View.VISIBLE
            }
        }
        like.setOnClickListener{
            Toast.makeText(activity,"added to Favourite list",Toast.LENGTH_SHORT).show()
            var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
            var favourites = ArrayList<Song>(0)
            var favouritesJson = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).getString("favourites",null)
            if (favouritesJson != null) {
                favourites.addAll(Gson().fromJson(favouritesJson, object : TypeToken<List<Song>>() {}.type))
                var suffix : String = seekbarValue.text.toString()
                if (seekbarValue.text.toString() == "0"){
                    suffix = if (isBible) " भूमिका" else ""
                } else {
                    suffix = " ".plus(suffix)
                }
                favourites.add(0, Song(null ,title!! + suffix , songView.text.toString()))
                editor.putString("favourites", Gson().toJson(favourites)).apply()
            } else {
                favourites.add(0, Song(null ,title!!, song!!))
                editor.putString("favourites", Gson().toJson(favourites)).apply()
            }

        }
        brighten.setOnClickListener {
            val windowAttribute = activity!!.window.attributes
            if (windowAttribute.screenBrightness <= 0.85) windowAttribute.screenBrightness += 0.2F
        }
        darken.setOnClickListener {
            val windowAttribute = activity!!.window.attributes
            if (windowAttribute.screenBrightness >= 0.15) windowAttribute.screenBrightness -= 0.2F
        }
        smaller.setOnClickListener {
            var fontSize  = songView.textSize / resources.displayMetrics.scaledDensity
            fontSize -= 1f
            songView.textSize = fontSize
        }
        bigger.setOnClickListener {
            var fontSize  = songView.textSize / resources.displayMetrics.scaledDensity
            fontSize += 1f
            songView.textSize = fontSize
        }
        plus.setOnClickListener {
            if (seekbar.progress < seekbar.max){
                seekbar.setProgress(seekbar.progress+1)
                seekbarValue.visibility = View.VISIBLE
                Handler().postDelayed({seekbarValue.visibility = View.GONE}, 800)
            }
        }
        minus.setOnClickListener {
            if (seekbar.progress > 1){
                seekbar.setProgress(seekbar.progress-1)
                seekbarValue.visibility = View.VISIBLE
                Handler().postDelayed({seekbarValue.visibility = View.GONE}, 800)
            }
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress >= 1){
                    seekbarValue.text = progress.toString()
                    songView.text = chapters[progress-1].song
                    //Log.d("data: ", "left "+ seekBar!!.left + " right " + seekBar.right + " progress% " + progress.toFloat()/seekBar.max)
                    var pos = seekBar!!.paddingLeft + seekbarValue.width/2 + ( seekBar.right - seekBar.paddingRight - seekBar.left - seekBar.paddingLeft - seekbarValue.width) * (progress.toFloat() /seekBar.max)
                    seekbarValue.x = pos
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekbarValue.visibility = View.VISIBLE
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekbarValue.visibility = View.GONE
            }
        })
    }

    private fun getChapters(book : String) : ArrayList<Song> {
        var chapters : ArrayList<Song> = ArrayList(0)
        try {
            val inputStream : InputStream = activity!!.assets.open("Bible$book.json")
            val inputString : String = inputStream.bufferedReader().use { it.readText() }
            val songsArray = JSONArray(inputString)
            for (i in 0.until(songsArray.length())){
                val song = songsArray.getJSONObject(i)
                chapters.add(Song(song.getInt("chapterNumber"),song.getString("chapterTitle"),song.getString("chapterContent")))
            }
        }catch (e : Exception){
            Log.d("Ex while opening file",e.toString())
        }
        return chapters
    }
}
