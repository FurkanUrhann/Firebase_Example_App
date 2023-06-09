package com.example.gezilecekyerler.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.gezilecekyerler.R
import com.example.gezilecekyerler.models.GeziAlani


class GeziAlanlariAdapter(private val context: Context, private val geziAlanlariList: List<GeziAlani>) : BaseAdapter() {

    override fun getCount(): Int {
        return geziAlanlariList.size
    }

    override fun getItem(position: Int): Any {
        return geziAlanlariList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_gezi_alani, parent, false)
        }

        val baslikTextView = view?.findViewById<TextView>(R.id.baslikTextView)
        val sehirTextView = view?.findViewById<TextView>(R.id.sehirTextView)
        val notlarTextView = view?.findViewById<TextView>(R.id.notlarTextView)

        val geziAlani = geziAlanlariList[position]
        baslikTextView?.text = "Başlık: ${geziAlani.baslik}"
        sehirTextView?.text = "Şehir: ${geziAlani.sehir}"
        notlarTextView?.text = "Notlar: ${geziAlani.notlar}"

        return view!!
    }

    private class ViewHolder(view: View) {
        private val baslikTextView: TextView = view.findViewById(R.id.baslikTextView)
        private val sehirTextView: TextView = view.findViewById(R.id.sehirTextView)
        private val notlarTextView: TextView = view.findViewById(R.id.notlarTextView)


        fun bind(geziAlani: GeziAlani) {
            baslikTextView.text = geziAlani.baslik
            sehirTextView.text = geziAlani.sehir
            notlarTextView.text = geziAlani.notlar
            baslikTextView.setTypeface(null, Typeface.BOLD)
            sehirTextView.setTypeface(null, Typeface.BOLD)
            notlarTextView.setTypeface(null, Typeface.BOLD)

        }
    }
}

