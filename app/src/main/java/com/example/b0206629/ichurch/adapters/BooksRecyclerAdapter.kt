package com.example.b0206629.ichurch.adapters

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.b0206629.ichurch.R
import com.example.b0206629.ichurch.models.Book
import kotlinx.android.synthetic.main.item_book.view.*
import kotlinx.android.synthetic.main.item_first_book.view.*



class BooksRecyclerAdapter(val books : ArrayList<Book>,
    val clickListener : (Book) -> Unit): RecyclerView.Adapter<BooksRecyclerAdapter.BookHolder>() {

    private val firstBook = 0
    private val book = 1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BookHolder {
        var inflateView = p0.inflate(R.layout.item_book)
        if (p1 == 0){
            inflateView = p0.inflate(R.layout.item_first_book)
        }
        return BookHolder(inflateView)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0){
            return firstBook
        } else {
            return book
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(p0: BookHolder, p1: Int) {
        val bookItem = books[p1]
        p0.bindBook(bookItem, p0.itemViewType, clickListener)
    }

    class BookHolder(v:View) : RecyclerView.ViewHolder(v){
        var view : View = v
        var book : Book? = null
        fun bindBook(book: Book, type: Int, clickListener: (Book) -> Unit){
            this.book = book
            if(type == 0){
                view.first_bookName.text = book.name
                view.first_image.setImageDrawable(book.image)
                view.setOnClickListener { clickListener(book) }
            } else{
                view.bookName.text = book.name
                view.image.setImageDrawable(book.image)
                view.setOnClickListener { clickListener(book) }
            }
        }
    }

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attatchToRoot : Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attatchToRoot)
    }
}