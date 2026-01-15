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
import androidx.core.widget.doAfterTextChanged
import com.example.loginsystem.DatabaseHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LogInActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        // ✅ Check saved session
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val savedUserId = prefs.getInt("USER_ID", -1)
        if (savedUserId != -1) {
            // Already logged in, go straight to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USER_ID", savedUserId)
            startActivity(intent)
            finish()
            return
        }

        val etUserName = findViewById<TextInputEditText>(R.id.etUserName)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val tiUserName = findViewById<TextInputLayout>(R.id.tiUserName)
        val tiPassword = findViewById<TextInputLayout>(R.id.tiPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login"

        // Clear errors while typing
        etUserName.doAfterTextChanged { tiUserName.error = null }
        etPassword.doAfterTextChanged { tiPassword.error = null }

        // Login button
        btnLogin.setOnClickListener {
            val usernameInput = etUserName.text.toString()
            val passwordInput = etPassword.text.toString()

            val userId = db.login(usernameInput, passwordInput)
            if (userId != -1) {
                // ✅ Save session
                prefs.edit().putInt("USER_ID", userId).apply()

                val username = db.getUsername(userId)
                Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
            } else {
                tiUserName.error = "Invalid username or password"
                tiPassword.error = "Invalid username or password"
            }
        }

        // Setup clickable "REGISTER" word in TextView
        val fullText = "Don't Have Account? REGISTER"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("REGISTER")
        val end = start + "REGISTER".length

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LogInActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = getColor(R.color.purple_500)
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvRegister.text = spannable
        tvRegister.movementMethod = LinkMovementMethod.getInstance()
    }
}





