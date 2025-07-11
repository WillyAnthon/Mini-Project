package com.willy.miniprojectsiasat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.willy.miniprojectsiasat.activity.KaprogdiActivity
import com.willy.miniprojectsiasat.databinding.FragmentLihatDataBinding

class LihatDataFragment : Fragment() {
    private var _binding: FragmentLihatDataBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLihatDataBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Register fragment ke activity untuk bisa menerima refresh callback
        (activity as? KaprogdiActivity)?.setLihatDataFragment(this)
        
        setupViewPager()
    }
    
    fun refreshData() {
        // Refresh semua tab data
        val adapter = binding.viewPagerData.adapter as? DataPagerAdapter
        adapter?.refreshAllFragments()
    }
    
    private fun setupViewPager() {
        val adapter = DataPagerAdapter(requireActivity(), this)
        binding.viewPagerData.adapter = adapter
        
        TabLayoutMediator(binding.tabLayoutData, binding.viewPagerData) { tab, position ->
            tab.text = when (position) {
                0 -> "Matakuliah"
                1 -> "Dosen"
                2 -> "Mahasiswa"
                else -> ""
            }
        }.attach()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private class DataPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val parentFragment: LihatDataFragment
    ) : FragmentStateAdapter(fragmentActivity) {
        
        private val fragments = mutableMapOf<Int, DataListFragment>()
        
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> DataListFragment.newInstance("matakuliah")
                1 -> DataListFragment.newInstance("dosen")
                2 -> DataListFragment.newInstance("mahasiswa")
                else -> throw IllegalArgumentException("Invalid position")
            }
            fragments[position] = fragment
            return fragment
        }
        
        fun refreshAllFragments() {
            fragments.values.forEach { fragment ->
                fragment.refreshData()
            }
        }
    }
} 