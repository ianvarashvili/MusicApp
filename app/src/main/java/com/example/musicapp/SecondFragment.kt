package com.example.musicapp

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.adapter.RecyclerViewAdapter
import com.example.musicapp.databinding.FragmentSecondBinding
import com.example.musicapp.models.Song
import com.google.firebase.firestore.FirebaseFirestore


class SecondFragment : Fragment(R.layout.fragment_second) {


    private lateinit var binding: FragmentSecondBinding
    private lateinit var adapter: RecyclerViewAdapter
    private val songs = mutableListOf<Song>()
    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSecondBinding.bind(view)

        adapter = RecyclerViewAdapter(
            songs,
            onPlayClick = { song -> playSong(song) },

        )


        binding.RV.layoutManager = LinearLayoutManager(requireContext())
        binding.RV.adapter = adapter

        fetchSongsFromFirebase()
    }

    private fun fetchSongsFromFirebase() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Songs")
            .get()
            .addOnSuccessListener { result ->
                Log.d("FirestoreCheck", "Documents fetched: ${result.size()}")
                songs.clear()
                for (doc in result) {
                    Log.d("FirestoreCheck", "Doc: ${doc.data}")
                    val song = doc.toObject(Song::class.java)
                    songs.add(song)
                }
                Log.d("FirestoreCheck", "Songs loaded: ${songs.size}")
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load songs", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error loading songs", e)
            }
    }


    private fun playSong(song: Song) {
        MainActivity.MusicPlayerManager.currentPlaylist = songs
        val index = songs.indexOf(song)
        MainActivity.MusicPlayerManager.currentIndex = index

        MainActivity.MusicPlayerManager.playSong(song, index)

        val mainFragment = parentFragment as? MainFragment ?: return
        mainFragment.openPlayerFromOutside(song)
    }



    companion object {
        fun newInstance(): SecondFragment {
            return SecondFragment()
        }
    }

}