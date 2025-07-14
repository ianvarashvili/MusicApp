package com.example.musicapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.models.Song

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.main, MainFragment.newInstance()).commit()
    }

    object MusicPlayerManager {
        var mediaPlayer: MediaPlayer? = null
        var currentSong: Song? = null
        var currentIndex: Int = -1
        var currentPlaylist: List<Song> = emptyList() // Add this
        fun playSong(song: Song) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(song.url)
                prepare()
                start()
            }
            currentSong = song
            currentIndex = currentPlaylist?.indexOf(song) ?: 0
        }
    }

}