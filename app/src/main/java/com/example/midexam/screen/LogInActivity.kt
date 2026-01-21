package com.example.midexam.screen

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged
import com.example.midexam.data.DatabaseHelper
import com.example.midexam.databinding.ActivityLoginBinding
import com.example.midexam.util.showToast

class LogInActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var prefs: SharedPreferences
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        handlePref()
        setToolbar()
        clearErrors()
        binding.btnLogin.setOnClickListener {
            login()
        }

        navigateToRegister()

    }

    private fun handlePref() {
        prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val savedUserId = prefs.getInt("USER_ID", -1)
        if (savedUserId != -1) {
            // Already logged in, go straight to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USER_ID", savedUserId)
            startActivity(intent)
            finish()
            return
        }
    }

    private fun setToolbar() = with(binding) {
        setSupportActionBar(toolbar.toolbar)
        supportActionBar?.title = "Login"
    }

    private fun clearErrors() = with(binding) {
        etUserName.doAfterTextChanged { tiUserName.error = null }
        etPassword.doAfterTextChanged { tiPassword.error = null }
    }

    fun login() = with(binding) {
        val usernameInput = etUserName.text.toString()
        val passwordInput = etPassword.text.toString()

        val userId = db.login(usernameInput, passwordInput)
        if (userId != -1) {
            prefs.edit { putInt("USER_ID", userId) }

            val username = db.getUsername(userId)
            showToast("Welcome $username")

            Intent(this@LogInActivity, HomeActivity::class.java).apply {
                putExtra("USER_ID", userId)
                startActivity(this)
                finish()
            }
        } else {
            tiUserName.error = "Invalid username or password"
            tiPassword.error = "Invalid username or password"
        }
    }

    private fun navigateToRegister() = with(binding.tvRegister) {
        text = SpannableString("REGISTER").apply {
            setSpan(UnderlineSpan(), 0, length, 0)
        }
        setOnClickListener {
            startActivity(Intent(this@LogInActivity, RegisterActivity::class.java))
            finish()
        }
    }
}





