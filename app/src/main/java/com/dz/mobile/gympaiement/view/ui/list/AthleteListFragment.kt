package com.dz.mobile.gympaiement.view.ui.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.databinding.AthleteFragmentBinding
import com.dz.mobile.gympaiement.view.Ext.makeToast
import com.dz.mobile.gympaiement.view.Ext.onTextChanged
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
                            makeToast("Detailed")
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