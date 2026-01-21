package com.example.midexam.screen

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.midexam.data.DatabaseHelper
import com.example.midexam.databinding.ActivityRegisterBinding
import com.example.midexam.util.showToast

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        setToolbar()
        clearErrors()
        binding.btnRegister.setOnClickListener {
            register()
        }
        navigateToLogin()

    }

    private fun setToolbar() = with(binding) {
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.title = "Register"
    }

    private fun clearErrors() = with(binding) {
        etUserName.doAfterTextChanged { tiUserName.error = null }
        etPassword.doAfterTextChanged { tiPassword.error = null }
        etCPassword.doAfterTextChanged { tiCPassword.error = null }
    }

    private fun register() = with(binding) {
        val user = etUserName.text.toString().trim()
        val pass = etPassword.text.toString()
        val confirm = etCPassword.text.toString()

        // Reset errors first
        tiUserName.error = null
        tiPassword.error = null
        tiCPassword.error = null

        // Validate
        if (!validateRegister(user, pass, confirm)) return

        // Try to register
        if (db.register(user, pass)) {
            showToast("Register successful")
            startActivity(Intent(this@RegisterActivity, LogInActivity::class.java))
            finish()
        } else {
            tiUserName.error = "Username already exists"
        }
    }

    private fun validateRegister(
        user: String,
        pass: String,
        confirm: String
    ): Boolean {
        var valid = true

        if (user.isBlank()) {
            binding.tiUserName.error = "Username required"
            valid = false
        }

        if (pass.isBlank()) {
            binding.tiPassword.error = "Password required"
            valid = false
        }

        if (confirm.isBlank()) {
            binding.tiCPassword.error = "Confirm password required"
            valid = false
        }

        if (pass != confirm) {
            binding.tiCPassword.error = "Passwords do not match"
            valid = false
        }

        return valid
    }

    private fun navigateToLogin() = with(binding.tvLogin) {
        text = SpannableString("LOGIN").apply {
            setSpan(UnderlineSpan(), 0, length, 0)
        }
        setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LogInActivity::class.java))
            finish()
        }
    }

}

