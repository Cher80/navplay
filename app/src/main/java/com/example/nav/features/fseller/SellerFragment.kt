package com.example.nav.features.fseller

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nav.databinding.FragmentSellerBinding
import com.example.nav.features.NavHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerFragment : Fragment() {

    private var _binding: FragmentSellerBinding? = null
    val viewModel by viewModels<SellerViewModel>()
    //val viewModel = ViewModelProvider(this).get(SellerViewModel::class.java)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("gnavlife", "onAttach Seller")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.i = savedInstanceState?.getInt("i") ?: 0
        Log.d("gnavlife", "onCreate Seller savedInstanceState=$savedInstanceState")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("gnavlife", "onCreateView Seller")
        viewModel.onCreateView()
        _binding = FragmentSellerBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        val navHelper = NavHelper(
            nav = viewModel.nav,
            pop = _binding!!.pop,
            home = _binding!!.home,
            dashboard = _binding!!.dashboard,
            notifications = _binding!!.notifications,
            seller = _binding!!.seller,
            product = _binding!!.product,
            company = _binding!!.company,
            plus = _binding!!.plus,
            onPlus = viewModel::plusI
        )
        navHelper.doBind()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("gnavlife", "onViewCreated Seller")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.iFlow.collect {
                _binding?.text?.text = "Seller $it"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("gnavlife", "onDestroyView Seller")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("gnavlife", "onDestroy Seller")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("gnavlife", "onDetach Seller")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("i",viewModel.i)
        Log.d("gnavlife", "onSaveInstanceState Seller outState=$outState")
        super.onSaveInstanceState(outState)
    }
}