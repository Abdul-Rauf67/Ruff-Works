package com.example.agrilinkup.View.Fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.HomeFragment
import com.example.agrilinkup.R
import com.example.agrilinkup.databinding.FragmentAddProductListingBinding
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddProductListingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddProductListingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentAddProductListingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_add_product_listing, container, false)

        binding= FragmentAddProductListingBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        var Seasons= arrayOf("Season","Spring","Summer","Fall(Autumn)","Winter")
        if (binding.SeasonSpinner!=null) {
            var adapter1= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,Seasons)
            binding.SeasonSpinner.adapter = adapter1
            1.also { binding.SeasonSpinner.id = it }
            binding.SeasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                    //Toast.makeText(requireContext(),"Spinner Position: ${position} and Season :${Seasons[position]}",Toast.LENGTH_LONG).show()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }

        binding.seasonStart.setOnClickListener(View.OnClickListener {
            var cal = Calendar.getInstance()
            var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                var month1 = month + 1
                var msg = "$dayOfMonth/$month1/$year"
                binding.seasonStart.text=msg
            }
            DatePickerDialog(requireContext(),dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        })
        binding.seasonEnd.setOnClickListener(View.OnClickListener {
            var cal = Calendar.getInstance()
            var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                var month1 = month + 1
                var msg = "$dayOfMonth/$month1/$year"
                binding.seasonEnd.text=msg
            }
            DatePickerDialog(requireContext(),dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        })



        binding.cancel.setOnClickListener {
            bactToHomeFragment()
        //findNavController().navigate(R.id.action_addProductListingFragment_to_homeFragment)
        }
    }
    fun bactToHomeFragment(){
        binding.fragmentAddProductListing.visibility = View.INVISIBLE
        val fragmentTansaction = childFragmentManager.beginTransaction()
        fragmentTansaction.replace(R.id.fragmentAddProdusts_Container, HomeFragment())
        fragmentTansaction.addToBackStack(null)
        fragmentTansaction.commit()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddProductListingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddProductListingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}