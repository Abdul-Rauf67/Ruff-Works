package com.example.agrilinkup.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.view.Adapters.AdapterAtCartRecyclerView
import com.example.agrilinkup.view.VmProfile
import com.example.agrilinkup.databinding.FragmentCartBinding
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val vmProfile: VmProfile by viewModels()
    @Inject lateinit var preferenceManager:PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        vmProfile.fetchProductsOfCart()

        binding.refreshCartItems.setOnRefreshListener {
            binding.refreshCartItems.isRefreshing=false
            vmProfile.fetchProductsOfCart()
            setUpObserver1()
        }
        setUpObserver()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addProductsToCart.setOnClickListener{
            (parentFragment as MainFragment)
                .switchToFragment(0)
        }
    }
    fun reFetchProduct(){
        vmProfile.fetchProductsOfCart()
        setUpObserver1()
    }

    private fun setUpObserver() {
        vmProfile.fetchCartProducts.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    val productsList = it.data
                  //  toast("productList Empty")
                    if (!productsList.isNullOrEmpty()) {
                        val navigator=findNavController()
                        binding.cartRecyclerView.adapter =
                            AdapterAtCartRecyclerView(productsList,requireContext(),
                                navigator,viewLifecycleOwner,childFragmentManager)
                    }
                    dismissProgressDialog()
                }

                is DataState.Error -> {
                    toast(it.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    showProgressDialog()
                }
            }
        }
    }

    private fun setUpObserver1() {
        vmProfile.fetchCartProducts.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    dismissProgressDialog()
                    val productsList = it.data
                    //  toast("productList Empty")
                    if (!productsList.isNullOrEmpty()) {
                        val navigator=findNavController()
                        binding.cartRecyclerView.adapter =
                            AdapterAtCartRecyclerView(productsList,requireContext(),
                                navigator,viewLifecycleOwner,childFragmentManager)
                    }
                    //dismissProgressDialog()
                }

                is DataState.Error -> {
                    toast(it.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                   // showProgressDialog()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}