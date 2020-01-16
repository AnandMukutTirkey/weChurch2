package com.example.b0206629.ichurch.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*

import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.adapters.BooksRecyclerAdapter
import com.example.b0206629.ichurch.models.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import java.io.InputStream
import java.lang.Exception
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    var adapter: BooksRecyclerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        books!!.layoutManager = LinearLayoutManager(activity)
        adapter = BooksRecyclerAdapter(getBooks(), {book -> bookClicked(book) })
        books.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
        editor.putString("lastFragment", "HomeFragment").apply()
    }

    private fun bookClicked(book: Book){
        fragmentManager!!.beginTransaction().setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.frame_container,SongsFragment.newInstance(book.file)).addToBackStack(null).commit()
    }

    fun getBooks() : ArrayList<Book>{
        var books = ArrayList<Book>(0)
        try {
            val inputStream : InputStream = activity!!.assets.open("Books.json")
            val inputString = inputStream.bufferedReader().use { it.readText() }
            val bookArray = JSONArray(inputString)
            for (i in 0.until(bookArray.length())){
                Log.d("name",bookArray.getJSONObject(i).getString("hindi name"))
                val book = bookArray.getJSONObject(i)
                val drawable : RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, activity!!.assets.open(book.getString("image")))
                drawable.cornerRadius = 10.toFloat()
                books.add(Book(book.getString("name"), book.getString("hindi name"), drawable, book.getInt("count"), book.getString("file")))
            }
        } catch (e : Exception){
            Log.d("Ex while opening file",e.toString())
        }
        var arrangement = ArrayList<Book>(0)
        var arrangementJson = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).getString("arrangement",null)?: return books
        arrangement.addAll(Gson().fromJson(arrangementJson, object : TypeToken<List<Book>>() {}.type))
        var arrangedBooks = ArrayList<Book>(0)
        for (i in 0.until(arrangement.size)){
            arrangedBooks.add(i,books[books.indexOf(Book(arrangement.get(i).name,"",null,0,""))])
        }
        return arrangedBooks
    }
}
