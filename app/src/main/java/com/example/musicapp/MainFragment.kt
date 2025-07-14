package com.example.musicapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicapp.databinding.FragmentMainBinding
import com.example.musicapp.models.Song


class MainFragment : Fragment() {

    lateinit var binding : FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater,container, false)
        return binding.root


    }



    private fun loadFragment(fragment: Fragment) {
        val current = parentFragmentManager.findFragmentById(R.id.placeHolder)
        if (current != null && current::class == fragment::class) return  // Already showing
        parentFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        // Load first fragment only ONCE when app starts
        if (parentFragmentManager.findFragmentById(R.id.placeHolder) == null) {
            loadFragment(FirstFragment.newInstance())
            bottomNavMenu.selectedItemId = R.id.home
        }

        // Keep BottomNavigationView in sync with current visible fragment
        parentFragmentManager.addOnBackStackChangedListener {
            val currentFragment = parentFragmentManager.findFragmentById(R.id.placeHolder)

            val selectedId = when (currentFragment) {
                is FirstFragment -> R.id.home
                is SecondFragment -> R.id.playlist
                is ThirdFragment -> R.id.player
                else -> R.id.home
            }

            if (bottomNavMenu.selectedItemId != selectedId) {
                bottomNavMenu.selectedItemId = selectedId
            }
        }

        // Handle tab clicks
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

    fun openPlayerFromOutside(song: Song) {
        val bundle = Bundle().apply {
            putParcelable("song", song)
        }

        val fragment = ThirdFragment()
        fragment.arguments = bundle

        loadFragment(fragment)


        binding.bottomNavMenu.menu.findItem(R.id.player).isChecked = true
    }


    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}