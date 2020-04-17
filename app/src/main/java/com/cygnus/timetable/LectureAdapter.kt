package com.cygnus.timetable

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.cygnus.model.Lecture

class LectureAdapter(context: Context, val models: List<Pair<String, Lecture>>)
    : ArrayAdapter<Pair<String, Lecture>>(context, android.R.layout.simple_list_item_2, models) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val model = models[position]
        var view = convertView

        if (view == null) {
            view = View.inflate(context, android.R.layout.simple_list_item_2, null)
            view.tag = view
        } else {
            view = view.tag as View
        }

        view?.findViewById<TextView>(android.R.id.text1)?.text = model.first
        view?.findViewById<TextView>(android.R.id.text2)?.text = model.second.toString()

        return view!!
    }

}