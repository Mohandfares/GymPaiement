package com.dz.mobile.gympaiement.view.ui.detailed

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.data.bo.Athlete
import com.dz.mobile.gympaiement.databinding.DetailedAthleteFragmentBinding
import com.dz.mobile.gympaiement.view.Ext.gone
import com.dz.mobile.gympaiement.view.Ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailedAthleteFragment : Fragment(R.layout.detailed_athlete_fragment) {

    private var _binding: DetailedAthleteFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailedViewModel by viewModels()
    private lateinit var paymentAthleteAdapter: PaymentAthleteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DetailedAthleteFragmentBinding.bind(view)
        paymentAthleteAdapter = PaymentAthleteAdapter()
        val athlete = arguments?.get("athlete") as Athlete
        binding.apply {
            paymentsRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = paymentAthleteAdapter
            }
            firstlastname.text = athlete.firstAndLastName
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                progressBar.visible()
                delay(1000)
                viewModel.getAthletePayments(athlete.idAthlete).collect { payments ->
                    paymentAthleteAdapter.submitList(payments)
                    progressBar.gone()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}