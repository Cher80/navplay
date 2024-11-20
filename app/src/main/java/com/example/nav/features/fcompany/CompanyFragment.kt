package com.example.nav.features.fcompany

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
import com.example.nav.databinding.FragmentCompanyBinding
import com.example.nav.features.NavHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompanyFragment : BaseFragment() {

    private var _binding: FragmentCompanyBinding? = null

    val viewModel by viewModels<CompanyViewModel>()
    //val viewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("gnavlife", "onAttach Company")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.i = savedInstanceState?.getInt("i") ?: 0
        Log.d("gnavlife", "onCreate Company savedInstanceState=$savedInstanceState")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("gnavlife", "onCreateView Company")
        viewModel.onCreateView()
        _binding = FragmentCompanyBinding.inflate(inflater, container, false)
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
        Log.d("gnavlife", "onViewCreated Company")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.iFlow.collect {
                _binding?.text?.text = "Company $it"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("gnavlife", "onDestroyView Company")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("gnavlife", "onDestroy Company")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("gnavlife", "onDetach Company")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("i",viewModel.i)
        Log.d("gnavlife", "onSaveInstanceState Company outState=$outState")
        super.onSaveInstanceState(outState)
    }
}