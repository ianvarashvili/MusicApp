package com.example.musicapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.FragmentThirdBinding
import com.example.musicapp.models.Song
import java.util.concurrent.TimeUnit

class ThirdFragment : Fragment(R.layout.fragment_third) {

    private lateinit var binding: FragmentThirdBinding
    private val handler = Handler(Looper.getMainLooper())



    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val player = MainActivity.MusicPlayerManager.mediaPlayer
            if (player != null && player.isPlaying) {
                val currentMillis = player.currentPosition
                binding.startTime.text = formatTime(currentMillis)

                val duration = player.duration
                if (duration > 0) {
                    val progress = (currentMillis * 100) / duration
                    binding.songSeekBar.progress = progress
                }

                handler.postDelayed(this, 1000)
            }
        }
    }
    private fun bounceView(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1.05f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1.05f, 1f)
        val set = AnimatorSet()
        set.playTogether(scaleX, scaleY)
        set.duration = 300
        set.start()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentThirdBinding.bind(view)
//        binding.btnPrev.setOnClickListener { playPrevSong() }
//        binding.btnNext.setOnClickListener { playNextSong() }
        val song = arguments?.getParcelable<Song>("song")
            ?: MainActivity.MusicPlayerManager.currentSong

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

        android.util.Log.d("ThirdFragment", "btnPrev visible: ${binding.btnPrev.visibility}, clickable: ${binding.btnPrev.isClickable}")
        android.util.Log.d("ThirdFragment", "btnNext visible: ${binding.btnNext.visibility}, clickable: ${binding.btnNext.isClickable}")

        // Set text fields
        binding.songTitle.text = song.title
        binding.artistName.text = song.artist

        // Load album image
        Glide.with(this)
            .load(song.image)
            .into(binding.img)

        val player = MainActivity.MusicPlayerManager.mediaPlayer

        if (player != null) {
            if (player.isPlaying) {
                val duration = player.duration
                binding.endTime.text = formatTime(duration)
                handler.post(updateTimeRunnable)
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

        binding.stopButton.setOnClickListener {
            val player = MainActivity.MusicPlayerManager.mediaPlayer
            if (player != null) {
                if (player.isPlaying) {
                    player.pause()
                    binding.stopButton.setImageResource(R.drawable.play)
                   // Hide dancing cat
                    binding.catGif.animate().alpha(0f).setDuration(200).withEndAction {
                        binding.catGif.visibility = View.GONE
                    }.start()
                } else {
                    player.start()
                    binding.stopButton.setImageResource(R.drawable.stop)
                    // 🔁 Start seekbar updates safely
                    handler.removeCallbacks(updateTimeRunnable)
                    handler.post(updateTimeRunnable)

                    // Show dancing cat with fade-in and bounce
                    binding.catGif.alpha = 0f
                    binding.catGif.visibility = View.VISIBLE
                    binding.catGif.animate().alpha(1f).setDuration(300).start()
                    bounceView(binding.catGif)
                }
            }
        }



        binding.songSeekBar.setOnSeekBarChangeListener(object :
            android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: android.widget.SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val player = MainActivity.MusicPlayerManager.mediaPlayer
                if (fromUser && player != null) {
                    val duration = player.duration
                    val newPosition = (progress * duration) / 100
                    player.seekTo(newPosition)
                    binding.startTime.text = formatTime(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })


        // Load the transparent dancing cat GIF into the ImageView
        Glide.with(this)
            .asGif()
            .load(R.drawable.cat)  // Replace with your actual gif resource name
            .into(binding.catGif)

        val playerr = MainActivity.MusicPlayerManager.mediaPlayer
        if (playerr != null && playerr.isPlaying) {
            binding.catGif.visibility = View.VISIBLE
            binding.catGif.alpha = 1f
        } else {
            binding.catGif.visibility = View.GONE
        }



    }

    private fun playNextSong() {


        val playlist = MainActivity.MusicPlayerManager.currentPlaylist ?: return
        val currentIndex = MainActivity.MusicPlayerManager.currentIndex

        val newIndex = if (currentIndex < playlist.size - 1) currentIndex + 1 else 0
        playSongAtIndex(newIndex)
    }

    private fun playPrevSong() {


        val playlist = MainActivity.MusicPlayerManager.currentPlaylist ?: return
        val currentIndex = MainActivity.MusicPlayerManager.currentIndex

        val newIndex = if (currentIndex > 0) currentIndex - 1 else playlist.size - 1
        playSongAtIndex(newIndex)
    }


    private fun playSongAtIndex(index: Int) {
        val playlist = MainActivity.MusicPlayerManager.currentPlaylist
        if (playlist.isNullOrEmpty() || index < 0 || index >= playlist.size) {

            return
        }

//        if (playlist.isNullOrEmpty() || index < 0 || index >= playlist.size) return

        val song = playlist[index]
        MainActivity.MusicPlayerManager.currentIndex = index
        MainActivity.MusicPlayerManager.playSong(song, index)


        // Update UI
        binding.songTitle.text = song.title
        binding.artistName.text = song.artist
        Glide.with(this).load(song.image).into(binding.img)

        // Reset timers
        binding.startTime.text = "0:00"
        binding.songSeekBar.progress = 0

        val player = MainActivity.MusicPlayerManager.mediaPlayer
        val duration = player?.duration ?: 0
        binding.endTime.text = formatTime(duration)

        // Start seekbar updates
        handler.removeCallbacks(updateTimeRunnable)
        handler.post(updateTimeRunnable)

        // Update play/pause button
        binding.stopButton.setImageResource(R.drawable.stop)

        // Animate cat
        binding.catGif.alpha = 0f
        binding.catGif.visibility = View.VISIBLE
        binding.catGif.animate().alpha(1f).setDuration(300).start()
        bounceView(binding.catGif)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimeRunnable)
    }

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
