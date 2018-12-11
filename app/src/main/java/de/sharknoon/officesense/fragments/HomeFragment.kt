package de.sharknoon.officesense.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.sharknoon.officesense.R
import de.sharknoon.officesense.models.RoomStates

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    fun changeRoomClimate(newState: RoomStates) {
        val imageView = view?.findViewById<ImageView>(R.id.smiley_image_view)
        imageView?.setImageResource(newState.icon)

        val textView = view?.findViewById<TextView>(R.id.info_text_view)
        textView?.text = newState.status
    }

}
