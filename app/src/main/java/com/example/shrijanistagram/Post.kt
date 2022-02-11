package com.example.shrijanistagram

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Post")
class Post : ParseObject() {

    fun getDescription(): String? {
        return getString(KEY_DESCRIPTION)
    }

    fun setDescription(Description: String){
        put(KEY_DESCRIPTION, Description)
    }

    fun getImage(): ParseFile?{
        return getParseFile(KEY_IMAGE)
    }

    fun setImage(parseFile: ParseFile){
        put(KEY_IMAGE, parseFile)
    }

    fun getUser(): ParseUser?{
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser){
        put(KEY_USER, user)
    }

    companion object{
        const val KEY_DESCRIPTION = "Description"
        const val KEY_IMAGE = "Image"
        const val KEY_USER = "User"
    }

}