package com.example.musicapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.musicapp.databinding.FragmentFirstBinding


class FirstFragment : Fragment() {
lateinit var binding: FragmentFirstBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val player= view.findViewById<ImageView>(R.id.player)
        val rotate= android.view.animation.AnimationUtils.loadAnimation(requireContext(),R.anim.rotate)
        player.startAnimation(rotate)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() = FirstFragment()
    }
}