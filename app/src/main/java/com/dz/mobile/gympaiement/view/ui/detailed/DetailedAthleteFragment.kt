package com.dz.mobile.gympaiement.view.ui.detailed

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.data.bo.Athlete
import com.dz.mobile.gympaiement.databinding.DetailedAthleteFragmentBinding
import com.dz.mobile.gympaiement.view.Ext.*
import com.dz.mobile.gympaiement.view.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DetailedAthleteFragment : Fragment(R.layout.detailed_athlete_fragment) {

    private var _binding: DetailedAthleteFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailedViewModel by viewModels()
    private lateinit var paymentAthleteAdapter: PaymentAthleteAdapter

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DetailedAthleteFragmentBinding.bind(view)
        paymentAthleteAdapter = PaymentAthleteAdapter()
        val athlete = arguments?.get("athlete") as Athlete
        binding.apply {
            add.setOnClickListener {
                dialogPayment(athlete)
            }
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
                    abonnementtermine.gone()
                    paymentAthleteAdapter.submitList(payments)
                    progressBar.gone()
                    if (payments[0].date?.toStringFormat() == Date().toStringFormat())
                        return@collect
                    val thisDay = Date().toStringFormat().split("-")
                    val lastPayment = payments[0].date?.toStringFormat()?.split("-")!!
                    if (lastPayment[0] == thisDay[0]) {
                        if (lastPayment[1] != thisDay[1] && thisDay[1].toInt() - lastPayment[1].toInt() == 1) {
                            if (thisDay[2].toInt() - lastPayment[2].toInt() >= 0) {
                                abonnementtermine.visible()
                            }
                        } else if (thisDay[1].toInt() - lastPayment[1].toInt() > 1) {
                            abonnementtermine.visible()
                        }
                    } else {
                        abonnementtermine.visible()
                    }
                }
            }
        }
    }

    private var jobSave: Job? = null
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun dialogPayment(athlete: Athlete) {
        val dialog = BottomSheetDialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_payment)
            val close = findViewById<ImageButton>(R.id.close)!!
            close.setOnClickListener { dismiss() }
        }
        val typepayment = dialog.findViewById<AutoCompleteTextView>(R.id.typepayment)!!
        val itemsTypePayment = resources.getStringArray(R.array.typepaymentvalues)
        val typePaymentAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, itemsTypePayment)
        typepayment.apply {
            setAdapter(typePaymentAdapter)
            setText(itemsTypePayment[0],false)
        }
        val tilmontant = dialog.findViewById<TextInputLayout>(R.id.tilmontant)!!
        val montant = dialog.findViewById<TextInputEditText>(R.id.montant)!!
        val confirmer = dialog.findViewById<Button>(R.id.confirmer)!!
        confirmer.setOnClickListener {
            jobSave?.cancel()
            jobSave = viewLifecycleOwner.lifecycleScope.launch {
                when (val save = viewModel.save(montant.value(),typepayment.value(),athlete)) {
                    is DetailedViewModel.SavePaymentResult.ErrorSavePayment ->
                        tilmontant.error = save.error
                    DetailedViewModel.SavePaymentResult.SaveWithSuccess -> {
                        dialog.dismiss()
                        requireContext().sendBroadcast(Intent(Constants.REFRESH_ACTION))
                    }
                    DetailedViewModel.SavePaymentResult.PaymentIsSaved ->
                        makeToast(getString(R.string.paiementexiste))
                }
            }
        }
        dialog.show()
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