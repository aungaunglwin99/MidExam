package com.example.midexam

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.example.loginsystem.DatabaseHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

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

        val tiUserName = findViewById<TextInputLayout>(R.id.tiUserName)
        val tiPassword = findViewById<TextInputLayout>(R.id.tiPassword)
        val tiCPassword = findViewById<TextInputLayout>(R.id.tiCPassword)

        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvGoLogin)

        // Clear errors while typing
        etUserName.doAfterTextChanged { tiUserName.error = null }
        etPassword.doAfterTextChanged { tiPassword.error = null }
        etCPassword.doAfterTextChanged { tiCPassword.error = null }

        // REGISTER button click
        btnRegister.setOnClickListener {
            val user = etUserName.text.toString().trim()
            val pass = etPassword.text.toString()
            val confirm = etCPassword.text.toString()

            // Reset errors
            tiUserName.error = null
            tiPassword.error = null
            tiCPassword.error = null

            var hasError = false

            if (user.isEmpty()) {
                tiUserName.error = "Username required"
                hasError = true
            }

            if (pass.isEmpty()) {
                tiPassword.error = "Password required"
                hasError = true
            }

            if (confirm.isEmpty()) {
                tiCPassword.error = "Confirm password required"
                hasError = true
            }

            if (hasError) return@setOnClickListener

            if (pass != confirm) {
                tiCPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            // Try to register
            if (db.register(user, pass)) {
                toast("Register successful")
                startActivity(Intent(this, LogInActivity::class.java))
                finish()
            } else {
                tiUserName.error = "Username already exists"
            }
        }

        // Clickable LOGIN text
        val fullText = "Already Have Account? LOGIN"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("LOGIN")
        val end = start + "LOGIN".length

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RegisterActivity, LogInActivity::class.java))
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.purple_500)
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvLogin.text = spannable
        tvLogin.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

