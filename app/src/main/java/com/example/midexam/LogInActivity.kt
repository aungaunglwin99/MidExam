package com.example.midexam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.loginsystem.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText

class LogInActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val etUserName = findViewById<TextInputEditText>(R.id.etUserName)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnGoRegister)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login"

        btnLogin.setOnClickListener {
            val id = db.login(
                etUserName.text.toString(),
                etPassword.text.toString()
            )

            if (id != -1) {
                val i = Intent(this, HomeActivity::class.java)
                i.putExtra("USER_ID", id)
                startActivity(i)
                finish()
            } else {
                toast("Invalid login")
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
