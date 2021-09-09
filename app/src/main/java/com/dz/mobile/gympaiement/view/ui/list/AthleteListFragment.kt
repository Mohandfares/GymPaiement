package com.dz.mobile.gympaiement.view.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.databinding.AthleteFragmentBinding
import com.dz.mobile.gympaiement.view.Ext.onTextChanged
import com.dz.mobile.gympaiement.view.ui.detailed.DetailedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AthleteListFragment : Fragment(R.layout.athlete_fragment) {

    private var _binding: AthleteFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var athleteAdapter: AthleteAdapter

    private val viewModel: AthleteViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AthleteFragmentBinding.bind(view)
        athleteAdapter = AthleteAdapter { athlete ->
            viewModel.navigationToDetailed(athlete)
        }
        binding.apply {
            add.setOnClickListener {
                viewModel.navigationToAdd()
            }
            research.onTextChanged { query ->
                viewModel.queryChange(query)
            }
            athleteRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = athleteAdapter
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.athletes.collect { athletes ->
                    athleteAdapter.submitList(athletes)
                    llemptystate.isVisible = athletes.isEmpty()
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.navigationEvents.collect { events ->
                    when (events) {
                        AthleteViewModel.NavigationEvent.NavigationAdd -> {
                            val action = AthleteListFragmentDirections.actionAthleteListFragmentToAddAthleteFragment()
                            findNavController().navigate(action)
                        }
                        is AthleteViewModel.NavigationEvent.NavigationDetailed -> {
                            val athlete = events.athlete
                            val intent = Intent(requireActivity(),DetailedActivity::class.java).apply {
                                putExtra("athlete",athlete)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}