package com.example.b0206629.ichurch.adapters

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.models.Note
import kotlinx.android.synthetic.main.item_list.view.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class NotesRecyclerAdapter (
        val notes : ArrayList<Note>,
        val clickListener: (Note, Int) -> Unit,
        val delete : (Int) -> Unit) : RecyclerView.Adapter<NotesRecyclerAdapter.NoteHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NoteHolder {
        val inflateView = p0.inflate(R.layout.item_list)
        return NoteHolder(inflateView)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(p0: NoteHolder, p1: Int) {
        p0.bindBook(notes, clickListener, p1, delete)
    }

    class NoteHolder(v:View) : RecyclerView.ViewHolder(v){
        var view : View = v
        var note : Note? = null

        fun bindBook(notes: ArrayList<Note>, clickListener: (Note, Int) -> Unit, index : Int, delete : (Int) -> Unit){
            this.note = notes[index]
            view.text.text = notes[index].title
            view.subtext.text = notes[index].description
            if (index == 0){
                view.delete.visibility = View.GONE
            }
            if (notes[index].updatedDate != null){
                view.date.text = SimpleDateFormat("dd-MM-yyyy").format(notes[index].updatedDate)
            } else {
                view.date.text = null
            }
            view.delete.setOnClickListener{ delete(index) }
            view.setOnClickListener { clickListener(notes[index], index) }
        }
    }

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attatchToRoot : Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attatchToRoot)
    }
}