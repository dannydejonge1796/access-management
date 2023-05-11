package com.example.access_management

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    //Get welcome text by id
    val txtWelcome: TextView = findViewById(R.id.txtWelcome)
    //Get parameters
    val firstName = intent.getStringExtra("firstname")
    val lastName = intent.getStringExtra("lastname")
    //Set welcome text
    val welcomeMessage = getString(R.string.welcome_message, firstName, lastName)
    txtWelcome.text = welcomeMessage

    //Get logout button by id
    val btnLogout: Button = findViewById(R.id.btnLogout)
    //Load login activity on click
    btnLogout.setOnClickListener {
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
      finish()
    }
  }
}