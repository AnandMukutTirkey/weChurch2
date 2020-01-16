package com.example.b0206629.ichurch.models

import android.support.v4.graphics.drawable.RoundedBitmapDrawable

data class Book (
        val name: String,
        val hindiName : String,
        val image : RoundedBitmapDrawable?,
        val count : Int,
        val file : String
){
    override fun equals(other: Any?): Boolean {
        if(!(other is Book)) return false;
        return other.name == name
    }
}