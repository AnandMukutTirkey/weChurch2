package com.example.b0206629.ichurch.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.models.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import java.util.*
import kotlin.collections.ArrayList

class NoteFragment : Fragment() {
    private var index: Int? = null
    private var notesString : String? = null
    private var notes : ArrayList<Note> = ArrayList(0)

    companion object {
        @JvmStatic
        fun newInstance(index : Int, notes : String) = NoteFragment().apply {
            arguments = Bundle().apply {
                putInt("index", index)
                putString("notes", notes)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt("index")
            notesString = it.getString("notes")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notes = Gson().fromJson(notesString, object : TypeToken<List<Note>>() {}.type)
        if (index!! >= 0) noteTitle.setText(notes[index!!].title) else noteTitle.setText("")
        if (index!! >= 0) noteSubTitle.setText(notes[index!!].description) else noteSubTitle.setText("")
        setActions(view)
    }

    override fun onPause() {
        super.onPause()
        var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
        editor.putString("lastFragment", "NotesFragment").apply()
    }

    private fun setActions(view : View){
        save.setOnClickListener {
            if (view.noteTitle.text.toString().isBlank() && view.noteSubTitle.text.toString().isBlank()) {
                fragmentManager!!.popBackStack()
                return@setOnClickListener
            }
            var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
            if (index!! >= 0){
                notes.set(index!!, Note(view.noteTitle.text.toString(), view.noteSubTitle.text.toString(), Date()))
            } else {
                notes.add(0, Note(view.noteTitle.text.toString(), view.noteSubTitle.text.toString(), Date()))
            }
            editor.putString("notes", Gson().toJson(notes)).apply()
            val imm : InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity!!.currentFocus != null) imm.hideSoftInputFromWindow(activity!!.currentFocus.windowToken, 0)
            fragmentManager!!.popBackStack()
        }
    }
}
