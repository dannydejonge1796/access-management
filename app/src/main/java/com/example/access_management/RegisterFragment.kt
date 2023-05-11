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

    //When submit button is pressed
    binding.btnRegister.setOnClickListener {
      //Validation status
      var isFormValid = true

      //Get value of text field
      val firstname = binding.tfFirstname.text.toString()
      //Check empty
      if (firstname.isEmpty()) {
        //Set field error
        binding.tfFirstname.error = "Please enter your first name"
        //Update validation status
        isFormValid = false
      }

      //Get value of text field
      val lastname = binding.tfLastname.text.toString()
      //Check empty
      if (lastname.isEmpty()) {
        //Set field error
        binding.tfLastname.error = "Please enter your last name"
        //Update validation status
        isFormValid = false
      }

      //Get value of text field
      val email = binding.tfEmail.text.toString()
      //Check empty and valid email
      if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        //Set field error
        binding.tfEmail.error = "Please enter a valid email address"
        //Update validation status
        isFormValid = false
      }

      //Get value of text field
      val username = binding.tfUser.text.toString()
      //Check empty and length < 6
      if (username.isEmpty() || username.length < 6) {
        //Set field error
        binding.tfUser.error = "Username must be at least 6 characters"
        //Update validation status
        isFormValid = false
      }

      //Get value of text field
      val password = binding.tfPass.text.toString()
      //Regex password pattern
      val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}\$".toRegex()
      //Check empty and check if password matches regular expression
      if (password.isEmpty() || !password.matches(passwordPattern)) {
        //Set field error
        binding.tfPass.error = "Password must be at least 8 characters and include at least one letter, one number, and one special character"
        //Update validation status
        isFormValid = false
      }

      //Check if validation is ok
      if (!isFormValid) {
        //Show error message
        Toast.makeText(this.requireContext(), "Please correct the errors above", Toast.LENGTH_SHORT).show()
      } else {
        //Init firestore
        val db = Firebase.firestore

        //Get current timestamp
        val calendar = Calendar.getInstance()
        val timestamp = calendar.time

        // Create a new user
        val user = hashMapOf(
          "firstname" to firstname,
          "lastname" to lastname,
          "email" to email,
          "username" to username,
          "password" to md5(password), //Hash password with md5 function
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

        //Success message
        Toast.makeText(this.requireContext(), "Your account has been registered", Toast.LENGTH_SHORT).show()
        //Return to login fragment
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