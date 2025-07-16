package com.example.musicapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.models.Song

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction() // Load MainFragment
            .replace(R.id.main, MainFragment.newInstance())
            .commit()
    }

    // globaluri funqcia
    object MusicPlayerManager {
        var mediaPlayer: MediaPlayer? = null  // MediaPlayer for the whole app
        var currentSong: Song? = null //currently playing song
        var currentIndex: Int = -1                       // current song index
        var Playlist: List<Song> = emptyList()


        fun playSong(song: Song, index: Int) {
            mediaPlayer?.release()

            // create new player
            mediaPlayer = MediaPlayer().apply {
                setDataSource(song.url)
                prepare()
                start()
            }

            // update
            currentSong = song
            currentIndex = index
        }
    }
}
