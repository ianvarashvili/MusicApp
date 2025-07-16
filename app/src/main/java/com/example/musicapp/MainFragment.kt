package com.example.musicapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicapp.databinding.FragmentMainBinding
import com.example.musicapp.models.Song

class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        // load FirstFragment first
        if (parentFragmentManager.findFragmentById(R.id.placeHolder) == null) {
            loadFragment(FirstFragment.newInstance())
            bottomNavMenu.selectedItemId = R.id.home //home icon
        }

        //backstack
        parentFragmentManager.addOnBackStackChangedListener {
            val currentFragment = parentFragmentManager.findFragmentById(R.id.placeHolder)//current fragment in placeholder

            val selectedId = when (currentFragment) { //selecting icons
                is FirstFragment -> R.id.home
                is SecondFragment -> R.id.playlist
                is ThirdFragment -> R.id.player
                else -> R.id.home
            }

            // if mismatch then update
            if (bottomNavMenu.selectedItemId != selectedId) {
                bottomNavMenu.selectedItemId = selectedId
            }
        }


        bottomNavMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(FirstFragment.newInstance())
                    true
                }
                R.id.playlist -> {
                    loadFragment(SecondFragment.newInstance())
                    true
                }
                R.id.player -> {
                    loadFragment(ThirdFragment.newInstance())
                    true
                }
                else -> false
            }
        }
    }


    private fun loadFragment(fragment: Fragment) {
        val current = parentFragmentManager.findFragmentById(R.id.placeHolder)//current fragment

        // skip if its already showing
        if (current != null && current::class == fragment::class) return //vadarebt types

        //replace current
        parentFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, fragment)
            .addToBackStack(null)
            .commit()
    }


    fun openPlayerFromOutside(song: Song) {
        //bundle with song data
        val bundle = Bundle().apply {
            putParcelable("song", song) // pass song to ThirdFragment
        }
        val fragment = ThirdFragment()
        fragment.arguments = bundle
        // open player
        loadFragment(fragment)
        //update botnav items
        binding.bottomNavMenu.menu.findItem(R.id.player).isChecked = true
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
