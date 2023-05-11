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

    val txtWelcome: TextView = findViewById(R.id.txtWelcome)
    val firstName = intent.getStringExtra("firstname")
    val lastName = intent.getStringExtra("lastname")
    val welcomeMessage = getString(R.string.welcome_message, firstName, lastName)
    txtWelcome.text = welcomeMessage

    val btnLogout: Button = findViewById(R.id.btnLogout)
    btnLogout.setOnClickListener {
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
      finish()
    }
  }
}