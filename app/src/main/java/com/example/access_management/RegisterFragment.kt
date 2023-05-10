package com.example.access_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.access_management.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

  private var _binding: FragmentRegisterBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentRegisterBinding.inflate(inflater, container, false)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

//    binding.buttonSecond.setOnClickListener {
//      findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}