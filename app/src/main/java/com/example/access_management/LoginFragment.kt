package com.example.access_management

import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.access_management.databinding.FragmentLoginBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest

class LoginFragment : Fragment() {

  private var _binding: FragmentLoginBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentLoginBinding.inflate(inflater, container, false)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.btnLogin.setOnClickListener {
      //Validation status
      var isFormValid = true

      //Get values from text fields
      val username = binding.tfUser.text.toString()
      val password = binding.tfPass.text.toString()

      //If username field is empty
      if (username.isEmpty()) {
        //Set field error
        binding.tfUser.error = "Please enter your username"
        //Set validation status
        isFormValid = false
      }

      //If password field is empty
      if (password.isEmpty()) {
        //Set field error
        binding.tfPass.error = "Please enter your password"
        //Set validation status
        isFormValid = false
      }

      //Check if form is successfully validated
      if (!isFormValid) {
        //Display error
        Toast.makeText(this.requireContext(), "Please correct the errors above", Toast.LENGTH_SHORT).show()
      } else {
        //Init firestore database
        val db = Firebase.firestore
        //Call db collection
        val collectionRef = db.collection("users")
        //Get the document which matches the filled in username
        collectionRef.whereEqualTo("username", username)
          .get()
          .addOnSuccessListener { documents ->
            if (documents.size() > 0) {
              val document = documents.first()
              //Check if account has been blocked
              if (document.getLong("login_failures")?.equals(3L) == true) {
                //Display message blocked account
                Toast.makeText(this.requireContext(), "Your account has been blocked", Toast.LENGTH_SHORT).show()
              } else {
                //Check if the filled in password matches the password in the database
                if (md5(password) == document.get("password")) {
                  val intent = Intent(activity, MainActivity::class.java).apply {
                    putExtra("firstname", document.get("firstname") as String)
                    putExtra("lastname", document.get("lastname") as String)
                  }
                  startActivity(intent)
                } else {
                  val docRef = collectionRef.document(document.id)

                  //Add 1 to the existing login failures
                  val loginFailures = (document.getLong("login_failures")?.toInt() ?: 0) + 1

                  //Update the value in the database
                  docRef.update("login_failures", loginFailures)
                    .addOnSuccessListener {
                      Log.d(TAG, "Document updated successfully.")
                    }
                    .addOnFailureListener { e ->
                      Log.w(TAG, "Error updating document.", e)
                    }

                  //Display failures left message
                  when (3 - loginFailures) {
                    2 -> {
                      Toast.makeText(
                        this.requireContext(),
                        "Login failed, you have 2 failures left", Toast.LENGTH_SHORT
                      ).show()
                    }

                    1 -> {
                      Toast.makeText(
                        this.requireContext(),
                        "Login failed, you have 1 failure left", Toast.LENGTH_SHORT
                      ).show()
                    }

                    0 -> {
                      //Get the current timestamp
                      val calendar = Calendar.getInstance()
                      val timestamp = calendar.time

                      //Set the timestamp for blocked_at in the database
                      docRef.update("blocked_at", timestamp)
                        .addOnSuccessListener {
                          Log.d(TAG, "Document updated successfully.")
                        }
                        .addOnFailureListener { e ->
                          Log.w(TAG, "Error updating document.", e)
                        }

                      Toast.makeText(
                        this.requireContext(),
                        "Login failed, your account has been blocked", Toast.LENGTH_SHORT
                      ).show()
                    }
                  }
                }
              }
            } else {
              Toast.makeText(this.requireContext(), "Unknown username", Toast.LENGTH_SHORT).show()
            }
          }
          .addOnFailureListener { exception ->
            Log.d(TAG, "Error getting documents: ", exception)
          }
      }
    }

    binding.btnRegister.setOnClickListener {
      findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
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

