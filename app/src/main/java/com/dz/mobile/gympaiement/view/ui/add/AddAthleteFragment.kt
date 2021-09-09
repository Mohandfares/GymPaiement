package com.dz.mobile.gympaiement.view.ui.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.databinding.AddAthleteFragmentBinding
import com.dz.mobile.gympaiement.view.Ext.makeToast
import com.dz.mobile.gympaiement.view.Ext.onTextChanged
import com.dz.mobile.gympaiement.view.Ext.toStringFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class AddAthleteFragment : Fragment(R.layout.add_athlete_fragment) {

    private var _binding: AddAthleteFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddAthleteViewModel by viewModels()

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AddAthleteFragmentBinding.bind(view)
        binding.apply {
            val itemsTypePayment = resources.getStringArray(R.array.typepaymentvalues)
            val typePaymentAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, itemsTypePayment)
            typepayment.apply {
                setAdapter(typePaymentAdapter)
                setText(itemsTypePayment[0],false)
                setOnItemClickListener { _, _, position, _ ->
                    viewModel.typePaymentChange(itemsTypePayment[position])
                }
            }
            date.setText(Date().toStringFormat())
            firstlastname.onTextChanged { viewModel.firstAndLastNameChange(it) }
            montant.onTextChanged { viewModel.amountChange(it) }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.firstAndLastNameState.collect {
                    tilfirstlastname.error = if (it.isBlank()) {
                        getString(R.string.errorfirstlastname)
                    } else {
                        null
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.amountState.collect {
                    tilmontant.error = if (it.isBlank()) {
                        getString(R.string.errormontant)
                    } else {
                        if (it.toInt() == 0) {
                            getString(R.string.errormontant)
                        } else {
                            null
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { events ->
                    when (events) {
                        AddAthleteViewModel.AddEvent.SaveError -> makeToast(getString(R.string.errorsave))
                        AddAthleteViewModel.AddEvent.SaveWithSuccess -> findNavController().popBackStack()
                    }
                }
            }
        }
        setHasOptionsMenu(true)
    }

    private var jobSave: Job? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_add_athlete,menu)
        val ok = menu.findItem(R.id.ok)
        ok.setOnMenuItemClickListener {
            jobSave?.cancel()
            jobSave = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.saveAthletePayment()
            }
            return@setOnMenuItemClickListener false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        jobSave?.cancel()
    }
}