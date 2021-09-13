package com.dz.mobile.gympaiement.view.ui.list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.databinding.AthleteFragmentBinding
import com.dz.mobile.gympaiement.view.Ext.gone
import com.dz.mobile.gympaiement.view.Ext.onTextChanged
import com.dz.mobile.gympaiement.view.Ext.visible
import com.dz.mobile.gympaiement.view.ui.detailed.DetailedActivity
import com.dz.mobile.gympaiement.view.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AthleteListFragment : Fragment(R.layout.athlete_fragment) {

    private var _binding: AthleteFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var athleteAdapter: AthleteAdapter
    private val viewModel: AthleteViewModel by viewModels()
    private var jobSearch: Job? = null

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
                progressBar.visible()
                jobSearch?.cancel()
                jobSearch = viewLifecycleOwner.lifecycleScope.launch {
                    delay(800L)
                    viewModel.queryChange(query)
                }
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
                    progressBar.gone()
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.navigationEvents.collect { events ->
                    when (events) {
                        AthleteViewModel.NavigationEvent.NavigationAdd -> {
                            val action =
                                AthleteListFragmentDirections.actionAthleteListFragmentToAddAthleteFragment()
                            findNavController().navigate(action)
                        }
                        is AthleteViewModel.NavigationEvent.NavigationDetailed -> {
                            val athlete = events.athlete
                            val intent =
                                Intent(requireActivity(), DetailedActivity::class.java).apply {
                                    putExtra("athlete", athlete)
                                }
                            startActivity(intent)
                        }
                    }
                }
            }
        }
        setHasOptionsMenu(true)
    }

    private lateinit var menuItem: MenuItem
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_alert, menu)
        menuItem = menu.findItem(R.id.alert)
        menuItem.setOnMenuItemClickListener {
            binding.progressBar.visible()
            viewModel.alertShowStateChange()
            return@setOnMenuItemClickListener false
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.alertShow.collect { state ->
                binding.apply {
                    abonnementtermine.isVisible = state
                    add.isVisible = !state
                }
                menuItem.icon = if (state) {
                    resources.getDrawable(R.drawable.ic_twotone_format_list_bulleted_24)
                } else {
                    resources.getDrawable(R.drawable.ic_twotone_notifications_24)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().registerReceiver(refreshReceiver, IntentFilter(Constants.REFRESH_ACTION))
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(refreshReceiver, IntentFilter(Constants.REFRESH_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        jobSearch?.cancel()
        requireContext().unregisterReceiver(refreshReceiver)
    }

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == Constants.REFRESH_ACTION) {
                    viewModel.refresh()
                }
            }
        }
    }
}