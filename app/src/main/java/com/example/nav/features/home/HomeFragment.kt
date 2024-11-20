package com.example.nav.features.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nav.base.BaseFragment
import com.example.nav.databinding.FragmentHomeBinding
import com.example.nav.features.NavHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val viewModel by viewModels<HomeViewModel>()
    //val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("gnavlife", "onAttach Home")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.i = savedInstanceState?.getInt("i") ?: 0
        val configuratorId = arguments?.getString("CONFIGURATOR_ID") ?: "empty"
        viewModel.configuratorId = configuratorId

        Log.d("gnavlife", "onCreate Home savedInstanceState=$savedInstanceState")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("gnavlife", "onCreateView Home")
        viewModel.onCreateView()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.camera.setOnClickListener {
            viewModel.nav.gotoCamera()
        }

        binding.cameraSber.setOnClickListener {
            viewModel.nav.gotoCameraSber()
        }

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
        Log.d("gnavlife", "onViewCreated Home")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.iFlow.collect {
                _binding?.text?.text = "Home $it"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("gnavlife", "onDestroyView Home")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("gnavlife", "onDestroy Home")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("gnavlife", "onDetach Home")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("i",viewModel.i)
        Log.d("gnavlife", "onSaveInstanceState Home outState=$outState")
        super.onSaveInstanceState(outState)
    }
}