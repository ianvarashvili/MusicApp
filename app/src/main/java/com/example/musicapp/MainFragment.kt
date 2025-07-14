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



    private fun loadFragment(f : Fragment){
        parentFragmentManager.beginTransaction().replace(R.id.placeHolder,f).addToBackStack(null).commit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding){
        super.onViewCreated(view, savedInstanceState)

        loadFragment(FirstFragment.newInstance())

        bottomNavMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home-> {
                    loadFragment(FirstFragment.newInstance())
                    true
                }

                R.id.playlist-> {
                    loadFragment(SecondFragment.newInstance())
                    true
                }
                R.id.player->{
                    loadFragment(ThirdFragment.newInstance())
                    true
                }


                else -> {
                    loadFragment(FirstFragment.newInstance())
                    true}
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