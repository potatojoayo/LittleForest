package com.benhan.bluegreen.localdata

import android.content.Context
import android.content.SharedPreferences

class SharedPreference {

    companion object{

        const val PREFERENCE_NAME = "user"
        const val DEFAULT_VALUE_STRING = ""
        const val DEFAULT_VALUE_BOOLEAN = false
        const val DEFAULT_VALUE_STRING_SET = ""

    }

    private fun getPreference(context: Context): SharedPreferences {


        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    }


    fun setString(context: Context, key: String, value: String){

        val prefs = getPreference(context)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()

    }

    fun setBoolean(context: Context, key: String, value: Boolean){

        val prefs = getPreference(context)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()

    }

    fun setInt(context: Context, key:String, value: Int){
        val prefs = getPreference(context)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()

    }

    fun getInt(context: Context, key:String): Int?{
        val prefs = getPreference(context)
        val value = prefs.getInt(key, 0)
        return value

    }


    fun setStringList(context: Context, key: String, arrayList: ArrayList<String>){

        val prefs = getPreference(context)
        val editor: SharedPreferences.Editor = prefs.edit()

        val set = HashSet<String>()
        set.addAll(arrayList)
        editor.putStringSet(key, set)
        editor.apply()

    }

    fun getStringList(context: Context, key: String): Set<String>{


        val hasSet = HashSet<String>()
        hasSet.add("")
        val prefs = getPreference(context)
        val value = prefs.getStringSet(key, hasSet)
        return value!!
    }

    fun getString(context: Context, key: String): String?{

        val prefs = getPreference(context)
        val value = prefs.getString(key,
            DEFAULT_VALUE_STRING
        )
        return value

    }


    fun getBoolean(context: Context, key: String): Boolean?{

        val prefs = getPreference(context)
        val value = prefs.getBoolean(key,
            DEFAULT_VALUE_BOOLEAN
        )
        return value

    }

    fun removeKey(context: Context, key: String){

        val prefs = getPreference(context)
        val edit = prefs.edit()
        edit.remove(key)
        edit.apply()

    }

    fun clear(context: Context){

        val prefs = getPreference(context)
        val edit = prefs.edit()
        edit.clear()
        edit.apply()
    }



}