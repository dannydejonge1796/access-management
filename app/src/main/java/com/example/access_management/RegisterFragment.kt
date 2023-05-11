package com.example.access_management

import android.content.ContentValues.TAG
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.access_management.databinding.FragmentRegisterBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest

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

    binding.btnRegister.setOnClickListener {
      var isFormValid = true

      val firstname = binding.tfFirstname.text.toString()
      if (firstname.isEmpty()) {
        binding.tfFirstname.error = "Please enter your first name"
        isFormValid = false
      }

      val lastname = binding.tfLastname.text.toString()
      if (lastname.isEmpty()) {
        binding.tfLastname.error = "Please enter your last name"
        isFormValid = false
      }

      val email = binding.tfEmail.text.toString()
      if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        binding.tfEmail.error = "Please enter a valid email address"
        isFormValid = false
      }

      val username = binding.tfUser.text.toString()
      if (username.isEmpty() || username.length < 6) {
        binding.tfUser.error = "Username must be at least 6 characters"
        isFormValid = false
      }

      val password = binding.tfPass.text.toString()
      val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}\$".toRegex()

      if (password.isEmpty() || !password.matches(passwordPattern)) {
        binding.tfPass.error = "Password must be at least 8 characters and include at least one letter, one number, and one special character"
        isFormValid = false
      }

      if (!isFormValid) {
        Toast.makeText(this.requireContext(), "Please correct the errors above", Toast.LENGTH_SHORT).show()
      } else {
        val db = Firebase.firestore

        val calendar = Calendar.getInstance()
        val timestamp = calendar.time

        // Create a new user
        val user = hashMapOf(
          "firstname" to firstname,
          "lastname" to lastname,
          "email" to email,
          "username" to username,
          "password" to md5(password),
          "verified_at" to timestamp,
          "login_failures" to 0,
          "blocked_at" to null
        )

        // Add a new document with a generated ID
        db.collection("users")
          .add(user)
          .addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
          }
          .addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
          }

        findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
      }
    }
  }

  private fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    val bytes = md.digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}