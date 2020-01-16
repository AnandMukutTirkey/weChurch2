package com.example.b0206629.ichurch.fragments

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.adapters.NotesRecyclerAdapter
import com.example.b0206629.ichurch.models.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class NotesFragment : Fragment() {
    private var notess : ArrayList<Note> = ArrayList(0)

    companion object {
        fun newInstance() = NotesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notess = getNotes()
        notess.add(0, Note("add New","your awesome note", null))
        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        notes.layoutManager = layoutManager
        notes.adapter = NotesRecyclerAdapter(notess, { note, index -> noteClicked(note, index)}, {index -> delete(index)})
    }

    override fun onPause() {
        super.onPause()
        var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
        editor.putString("lastFragment", "NotesFragment").apply()
    }

    private fun noteClicked(note: Note, index : Int){
        Log.d("tag" ,"note clicked " + note.title + " index " + index)
        var notesForEdit = ArrayList<Note>()
        notesForEdit.addAll(notess)
        notesForEdit.removeAt(0)
        fragmentManager!!.beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.frame_container, NoteFragment.newInstance(index-1, notes = Gson().toJson(notesForEdit)))
                .addToBackStack(null).commit()
    }

    private fun delete(index: Int){
        Log.d("tag" ,"delete index " + index)
        var alert = AlertDialog.Builder(activity).create()
        alert.setTitle("Warning")
        alert.setMessage("Are you sure to permanently delete this note")
        alert.setButton(AlertDialog.BUTTON_POSITIVE,"Yes"){ dialog, which ->
            notess.removeAt(index)
            var notesForSave = ArrayList<Note>(0)
            notesForSave.addAll(notess)
            notesForSave.removeAt(0)
            var editor = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).edit()
            editor.putString("notes", Gson().toJson(notesForSave)).apply()
            notes.adapter!!.notifyDataSetChanged()
        }
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){ dialog, which -> }
        alert.show()

    }

    private fun getNotes() : ArrayList<Note>{
        var notes : ArrayList<Note> = ArrayList(0)
        var notesJson = activity!!.getSharedPreferences("appData", Context.MODE_PRIVATE).getString("notes",null) ?: return notes
        notes.addAll(Gson().fromJson(notesJson, object : TypeToken<List<Note>>() {}.type))
        //TODO sort based on updated date before returning
        notes.sortWith(object  : Comparator<Note> {
            override fun compare(o1: Note?, o2: Note?): Int = when {
                o1!!.updatedDate!!.after(o2!!.updatedDate) -> -1
                o1.updatedDate!!.after(o2.updatedDate) -> 0
                else -> 1
            }
        })
        return notes
    }
}
