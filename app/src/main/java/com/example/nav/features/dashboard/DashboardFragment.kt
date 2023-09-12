package com.example.nav.features.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.detmir.recycli.adapters.bindState
import com.example.nav.base.observe
import com.example.nav.databinding.FragmentDashboardBinding
import com.example.nav.features.NavHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //val viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
    val viewModel by viewModels<DashboardViewModel>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("gnavlife", "onAttach Dashboard")
        viewModel.hashCode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.i = savedInstanceState?.getInt("i") ?: 0
        Log.d("gnavlife", "onCreate Dashboard savedInstanceState=$savedInstanceState")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("gnavlife", "onCreateView Dashboard")
        viewModel.onCreateView()
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
        Log.d("gnavlife", "onViewCreated Dashboard")
        _binding?.recycler?.layoutManager = LinearLayoutManager(requireContext())
        viewModel.iFlow.observe(this) {
            _binding?.text?.text = "Dashboard $it"
        }

        viewModel.recyclerState.observe(this) {
            _binding?.recycler?.bindState(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("gnavlife", "onDestroyView Dashboard")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("gnavlife", "onDestroy Dashboard")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("gnavlife", "onDetach Dashboard")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("i",viewModel.i)
        Log.d("gnavlife", "onSaveInstanceState Dashboard outState=$outState")
        super.onSaveInstanceState(outState)
    }
}