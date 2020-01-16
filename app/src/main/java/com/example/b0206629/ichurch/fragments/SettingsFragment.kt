package com.example.b0206629.ichurch.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log

import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.adapters.SettingsRecyclerAdapter
import com.example.b0206629.ichurch.models.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_settings.*
import org.json.JSONArray
import java.io.InputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import android.view.*

class SettingsFragment : Fragment() {

    var arrangedBooks : ArrayList<Book>? = null
    var touchHelper : ItemTouchHelper? = null
    var adapter : SettingsRecyclerAdapter? = null
    var theme : String = "light"
    var isAutomatic = true
    var settingTheView = true

    companion object {
        fun newInstance(theme : String, isAutomatic : Boolean) = SettingsFragment().apply { arguments = Bundle().apply {
            putString("theme", theme)
            putBoolean("isAutomatic", isAutomatic)
        } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            theme = it.getString("theme")
            isAutomatic = it.getBoolean("isAutomatic")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings.layoutManager = LinearLayoutManager(activity)
        arrangedBooks = getBooksArranged()
        adapter = SettingsRecyclerAdapter(arrangedBooks!!)
        settings.adapter = adapter
        scrollView.smoothScrollTo(0,0)
        setUpAutomaticSwitch()
        setupRadioButton()
        setItemTouchHelper()
        if (isAutomatic){
            Log.d("automatic", "enabled")
            automatic.isChecked = true
            var timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (timeOfDay >= 18 || timeOfDay <= 6){
                Log.d("time", "its night")
                theme = "dark"
            } else {
                Log.d("time", "its day")
                theme = "light"
            }
        } else {
            Log.d("automatic", "disabled")
        }
        when(theme){
            "light"->{
                settingTheView = true
                radioGroup.check(R.id.radioLight)
            }
            "dark"->{
                settingTheView = true
                radioGroup.check(R.id.radioDark)
            }
        }
        settingTheView = false
    }

    override fun onPause() {
        super.onPause()
        var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
        editor.putString("arrangement", Gson().toJson(arrangedBooks)).apply()
        editor.putString("lastFragment", "SettingsFragment").apply()
    }

    private fun setUpAutomaticSwitch(){
        automatic.setOnClickListener {
            if (automatic.isChecked){
                var timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                if (timeOfDay >= 18|| timeOfDay <= 6){
                    activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit().putBoolean("isAutomatic", true).putString("theme","dark").apply()
                    activity!!.recreate()
                } else {
                    activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit().putBoolean("isAutomatic", true).putString("theme","light").apply()
                    activity!!.recreate()
                }
            } else {
                activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit().putBoolean("isAutomatic", false).apply()
                activity!!.recreate()
            }
        }
    }

    private fun setupRadioButton(){
        radioGroup.setOnCheckedChangeListener{group, checkedId ->
            if (settingTheView) return@setOnCheckedChangeListener
            if (checkedId == R.id.radioDark){
                //activity!!.setTheme(R.style.DarkTheme)
                //fragmentManager!!.beginTransaction().replace(R.id.frame_container, newInstance()).commit()
                activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit().putBoolean("isAutomatic", false).putString("theme","dark").apply()
                activity!!.recreate()
            } else if (checkedId == R.id.radioLight){
                //activity!!.setTheme(R.style.AppTheme)
                //fragmentManager!!.beginTransaction().replace(R.id.frame_container, newInstance()).commit()
                activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit().putBoolean("isAutomatic", false).putString("theme","light").apply()
                activity!!.recreate()
            }
        }
    }

    private fun getBooksArranged() : ArrayList<Book>{
        var books = ArrayList<Book>(0)
        var booksJson = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).getString("arrangement",null)
        if(booksJson == null){
            try {
                val inputStream : InputStream = activity!!.assets.open("Books.json")
                val inputString = inputStream.bufferedReader().use { it.readText() }
                val bookArray = JSONArray(inputString)
                for (i in 0.until(bookArray.length())){
                    val book = bookArray.getJSONObject(i)
                    books.add(Book(book.getString("name"), "", null, i, ""))
                }
            } catch (e : Exception){
                Log.d("Ex while opening file",e.toString())
            }
        } else{
            books.addAll(Gson().fromJson(booksJson, object : TypeToken<List<Book>>() {}.type))
        }
        return books
    }

    private fun setItemTouchHelper(){
        touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0){
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                val sourcePosition = p1.adapterPosition
                val targetPosition = p2.adapterPosition
                //Log.d("tag","start "+ sourcePosition + " target " + targetPosition)
                if (sourcePosition < targetPosition) {
                    for (i in sourcePosition until targetPosition) {
                        Collections.swap(arrangedBooks, i, i + 1)
                    }
                } else {
                    for (i in sourcePosition downTo targetPosition + 1) {
                        Collections.swap(arrangedBooks, i, i - 1)
                    }
                }
                adapter?.notifyItemMoved(sourcePosition, targetPosition)
                return true
            }
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) { }
        })
        touchHelper?.attachToRecyclerView(settings)
    }
}
