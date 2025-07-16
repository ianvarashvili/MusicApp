package com.example.musicapp

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import android.view.View

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.adapter.RecyclerViewAdapter
import com.example.musicapp.databinding.FragmentSecondBinding
import com.example.musicapp.models.Song
import com.google.firebase.firestore.FirebaseFirestore

class SecondFragment : Fragment(R.layout.fragment_second) {

    private lateinit var binding: FragmentSecondBinding
    private lateinit var adapter: RecyclerViewAdapter
    private val songs = mutableListOf<Song>()        //songs list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSecondBinding.bind(view)
        adapter = RecyclerViewAdapter(songs, onPlayClick = { song -> playSong(song) } )


        //connecting rv to adapter
        binding.RV.layoutManager = LinearLayoutManager(requireContext())
        binding.RV.adapter = adapter

        //load data from firebase :)
        fetchSongsFromFirebase()
    }
    private fun fetchSongsFromFirebase() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Songs")
            .get()
            .addOnSuccessListener { result ->
                songs.clear()
                for (doc in result) {
                    val song = doc.toObject(Song::class.java) //convert firestore to Song obj
                    songs.add(song)
                }
                adapter.notifyDataSetChanged() //update adapterr
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load songs", Toast.LENGTH_SHORT).show()
            }
    }


    private fun playSong(song: Song) {
        MainActivity.MusicPlayerManager.Playlist = songs      //update global playlist
        val index = songs.indexOf(song)                              //get index
        MainActivity.MusicPlayerManager.currentIndex = index         // set index

        MainActivity.MusicPlayerManager.playSong(song, index)        // start playing the song

        val mainFragment = parentFragment as? MainFragment ?: return
        mainFragment.openPlayerFromOutside(song)                     //open player fragment
    }

    companion object {
        fun newInstance(): SecondFragment {
            return SecondFragment()
        }
    }
}
