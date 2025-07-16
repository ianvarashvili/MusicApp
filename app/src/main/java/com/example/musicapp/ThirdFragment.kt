package com.example.musicapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.FragmentThirdBinding
import com.example.musicapp.models.Song
import java.util.concurrent.TimeUnit

class ThirdFragment : Fragment(R.layout.fragment_third) {

    private lateinit var binding: FragmentThirdBinding
    private val handler = Handler(Looper.getMainLooper()) //handler for ui updates

    private fun showCat(show: Boolean) {
        if (show) {
            binding.catGif.alpha = 0f
            binding.catGif.visibility = View.VISIBLE
            binding.catGif.animate().alpha(1f).setDuration(300).start()
        } else {
            binding.catGif.animate().alpha(0f).setDuration(200).withEndAction {
                binding.catGif.visibility = View.GONE
            }.start()
        }
    }

    private val updateTimeRunnable = object : Runnable { //seekbar func
        override fun run() {
            val player = MainActivity.MusicPlayerManager.mediaPlayer
            if (player != null && player.isPlaying) {
                val currentMillis = player.currentPosition
                binding.startTime.text = formatTime(currentMillis) // update current time text
                //update seekbar progress
                val duration = player.duration
                if (duration > 0) {
                    val progress = (currentMillis * 100) / duration
                    binding.songSeekBar.progress = progress
                }

                handler.postDelayed(this, 1000) //next update in 1 second
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentThirdBinding.bind(view)

        // get passed songs
        val song = arguments?.getParcelable<Song>("song")
            ?: MainActivity.MusicPlayerManager.currentSong

        //tu musika araa archeuli gamochndes noSongMessage da moon
        if (song == null) {
            binding.playerContent.visibility = View.GONE
            binding.noSongMessage.visibility = View.VISIBLE
            binding.moon.visibility = View.VISIBLE
            return
        } else {
            binding.playerContent.visibility = View.VISIBLE
            binding.noSongMessage.visibility = View.GONE
            binding.moon.visibility = View.GONE
        }


        binding.btnNext.setOnClickListener {
            Log.d("ThirdFragment", "NEXT CLICKED")
            playNextSong()
        }

        binding.btnPrev.setOnClickListener {
            Log.d("ThirdFragment", "PREV CLICKED")
            playPrevSong()
        }


        // Update song details on screen
        binding.songTitle.text = song.title
        binding.artistName.text = song.artist


        Glide.with(this)
            .load(song.image)
            .into(binding.img)

        val player = MainActivity.MusicPlayerManager.mediaPlayer

        if (player != null) {
            if (player.isPlaying) {
                binding.endTime.text = formatTime(player.duration)
                handler.post(updateTimeRunnable)  // update time,seekbar
                binding.stopButton.setImageResource(R.drawable.stop)
            } else {
                binding.startTime.text = "0:00"
                binding.endTime.text = formatTime(player.duration)
                binding.stopButton.setImageResource(R.drawable.play)
            }
        } else {
            binding.startTime.text = "0:00"
            binding.endTime.text = "?:??"
            binding.stopButton.setImageResource(R.drawable.play)
        }
        //initial cat visibility
        showCat(MainActivity.MusicPlayerManager.mediaPlayer?.isPlaying == true)

        binding.stopButton.setOnClickListener {
            val player = MainActivity.MusicPlayerManager.mediaPlayer
            if (player != null) {
                if (player.isPlaying) {
                    player.pause()
                    binding.stopButton.setImageResource(R.drawable.play)


                    showCat(false)
                } else {
                    player.start()
                    binding.stopButton.setImageResource(R.drawable.stop)

                    //restart progress
                    handler.removeCallbacks(updateTimeRunnable)
                    handler.post(updateTimeRunnable)

                    showCat(true)

                }
            }
        }

        //user clicking the seekbar
        binding.songSeekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: android.widget.SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val player = MainActivity.MusicPlayerManager.mediaPlayer
                if (fromUser && player != null) { // only respond to user
                    val duration = player.duration
                    val newPosition = (progress * duration) / 100 // calculate new position
                    player.seekTo(newPosition) //jump there
                    binding.startTime.text = formatTime(newPosition) //and update
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })


        Glide.with(this).asGif().load(R.drawable.cat).into(binding.catGif)


    }


    private fun playNextSong() {
        val playlist = MainActivity.MusicPlayerManager.Playlist ?: return
        val currentIndex = MainActivity.MusicPlayerManager.currentIndex

        val newIndex = if (currentIndex < playlist.size - 1) currentIndex + 1 else 0
        playSongAtIndex(newIndex)
    }


    private fun playPrevSong() {
        val playlist = MainActivity.MusicPlayerManager.Playlist ?: return
        val currentIndex = MainActivity.MusicPlayerManager.currentIndex

        val newIndex = if (currentIndex > 0) currentIndex - 1 else playlist.size - 1
        playSongAtIndex(newIndex)
    }


    private fun playSongAtIndex(index: Int) {
        val playlist = MainActivity.MusicPlayerManager.Playlist
        if (playlist.isNullOrEmpty() || index < 0 || index >= playlist.size) {
            return
        }

        val song = playlist[index]
        // update global player state
        MainActivity.MusicPlayerManager.currentIndex = index
        MainActivity.MusicPlayerManager.playSong(song, index)

        // update song info
        binding.songTitle.text = song.title
        binding.artistName.text = song.artist
        Glide.with(this).load(song.image).into(binding.img)

        // reset
        binding.startTime.text = "0:00"
        binding.songSeekBar.progress = 0

        val player = MainActivity.MusicPlayerManager.mediaPlayer
        val duration = player?.duration ?: 0
        binding.endTime.text = formatTime(duration) //update duration

        // restart seekbar
        handler.removeCallbacks(updateTimeRunnable)
        handler.post(updateTimeRunnable)

        binding.stopButton.setImageResource(R.drawable.stop)

        showCat(true)
    }

    // Stop seekbar updates when view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimeRunnable)
    }

    //milliseconds into mins and secs
    private fun formatTime(ms: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ThirdFragment()
    }
}
