package com.example.b0206629.ichurch.adapters

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.models.Book
import kotlinx.android.synthetic.main.item_setting.view.*

class SettingsRecyclerAdapter (private val books : ArrayList<Book>) : RecyclerView.Adapter<SettingsRecyclerAdapter.BookHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BookHolder {
        val inflateView = p0.inflate(R.layout.item_setting)
        return BookHolder(inflateView)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(p0: BookHolder, p1: Int) {
        val bookItem = books[p1]
        p0.bindBook(bookItem)
    }
    class BookHolder(v: View) : RecyclerView.ViewHolder(v){
        var view : View = v
        var book : Book? = null
        fun bindBook(book: Book){
            this.book = book
            view.settingBookName.text = book.name
        }
    }
    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attatchToRoot : Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attatchToRoot)
    }
}