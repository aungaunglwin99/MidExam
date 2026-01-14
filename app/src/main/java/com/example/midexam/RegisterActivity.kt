package com.example.midexam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.loginsystem.DatabaseHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register"

        db = DatabaseHelper(this)

        val etUserName = findViewById<TextInputEditText>(R.id.etUserName)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etCPassword = findViewById<TextInputEditText>(R.id.etCPassword)

        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val btnLogin = findViewById<MaterialButton>(R.id.btnGoLogin)

        btnRegister.setOnClickListener {
            val user = etUserName.text.toString()
            val pass = etPassword.text.toString()
            val confirm = etCPassword.text.toString()

            when {
                user.isEmpty() || pass.isEmpty() || confirm.isEmpty() ->
                    toast("All fields required")

                pass != confirm ->
                    toast("Password not match")

                else -> {
                    if (db.register(user, pass)) {
                        toast("Register successful")
                        startActivity(Intent(this, LogInActivity::class.java))
                        finish()
                    } else {
                        toast("Username already exists")
                    }
                }
            }
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
