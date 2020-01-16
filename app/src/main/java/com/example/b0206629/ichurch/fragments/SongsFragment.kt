package com.example.b0206629.ichurch.fragments
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager

import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.adapters.SongsRecyclerAdapter
import com.example.b0206629.ichurch.models.Song
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlinx.android.synthetic.main.fragment_songs.view.*
import org.json.JSONArray
import java.io.InputStream
import java.lang.Exception

class SongsFragment : Fragment() {

    private var book : String? = null
    private var adapter : SongsRecyclerAdapter? = null
    private var queryTextListener : SearchView.OnQueryTextListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            book = it.get("selectedBook") as String
        }
        setHasOptionsMenu(true)
    }
    companion object {
        @JvmStatic fun newInstance(selectedBook : String) = SongsFragment().apply {
            arguments = Bundle().apply {
                putString("selectedBook",selectedBook)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_songs, container, false)
        adapter = SongsRecyclerAdapter(getSongs(book!!), {song -> songClicked(song) })
        view.songs.adapter = adapter
        view.songs.layoutManager = LinearLayoutManager(activity)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (searchView != null){
            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    adapter!!.filter.filter(newText)
                    return false
                }
                override fun onQueryTextSubmit(query: String): Boolean {
                    adapter!!.filter.filter(query)
                    return false
                }

            }
            searchView.
            searchView.setOnQueryTextListener(queryTextListener)
            searchView.setOnCloseListener(object : SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    songs.smoothScrollToPosition(0)
                    return false
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPause() {
        super.onPause()
        var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
        editor.putString("lastFragment", "HomeFragment").apply()
    }

    private fun songClicked(song: Song){
        searchView.setQuery("",false)
        val imm : InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity!!.currentFocus != null) imm.hideSoftInputFromWindow(activity!!.currentFocus.windowToken, 0)
        var isBible : Boolean = book == "Bible"
        fragmentManager!!.beginTransaction().setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.frame_container,SongFragment.newInstance(song.title, song.song, true, isBible)).addToBackStack(null).commit()
    }

    private fun getSongs(bookName : String): ArrayList<Song>{
        var songs : ArrayList<Song> = ArrayList(0)
        try {
            val inputStream : InputStream = activity!!.assets.open("$bookName.json")
            val inputString : String = inputStream.bufferedReader().use { it.readText() }
            val songsArray = JSONArray(inputString)
            for (i in 0.until(songsArray.length())){
                val song = songsArray.getJSONObject(i)
                if (bookName == "Bible"){
                    songs.add(Song(null,song.getString("title"),song.getString("description")))
                } else {
                    songs.add(Song(song.getInt("number"),song.getString("title"),song.getString("song")))
                }
            }
        }catch (e : Exception){
            Log.d("Ex while opening file",e.toString())
        }
        return songs
    }
}
