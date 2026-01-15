package com.example.midexam

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsystem.DatabaseHelper
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var userId = -1
    private lateinit var adapter: StatusAdapter
    private lateinit var list: MutableList<Status>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ✅ Toolbar setup
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"

        db = DatabaseHelper(this)

        // ✅ Get user ID from intent or session
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        userId = intent.getIntExtra("USER_ID", prefs.getInt("USER_ID", -1))

        if (userId == -1) {
            // No logged-in user, return to login
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
            return
        }

        val etStatus = findViewById<EditText>(R.id.etStatus)
        val btnUpload = findViewById<MaterialButton>(R.id.btnUpload)
        val rv = findViewById<RecyclerView>(R.id.rvStatus)

        // ✅ Load existing statuses
        list = db.getStatuses(userId)
        adapter = StatusAdapter(list,
            onEditClick = { status -> showEditDialog(status) },
            onDeleteClick = { status -> deleteStatus(status) }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // ✅ Upload button
        btnUpload.setOnClickListener {
            val text = etStatus.text.toString().trim()
            if (text.isEmpty()) {
                toast("Enter status")
            } else {
                val newStatus = db.addStatus(userId, text)
                list.add(newStatus)
                adapter.notifyItemInserted(list.size - 1)
                rv.scrollToPosition(list.size - 1)
                etStatus.setText("")
                toast("You Post A Status")
            }
        }

    }

    // ✅ Menu with logout
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ✅ Logout with session clear
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                // Clear saved session
                getSharedPreferences("login_prefs", MODE_PRIVATE).edit().clear().apply()
                val intent = Intent(this, LogInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ✅ Edit a status
    private fun showEditDialog(status: Status) {
        val et = EditText(this)
        et.setText(status.text)

        AlertDialog.Builder(this)
            .setTitle("Edit Status")
            .setView(et)
            .setPositiveButton("Save") { _, _ ->
                val newText = et.text.toString().trim()
                if (newText.isNotEmpty()) {
                    db.updateStatus(status.id, newText)
                    status.text = newText
                    adapter.notifyDataSetChanged()
                    toast("Status updated")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ✅ Delete a status
    private fun deleteStatus(status: Status) {
        AlertDialog.Builder(this)
            .setTitle("Delete Status")
            .setMessage("Are you sure you want to delete this status?")
            .setPositiveButton("Yes") { _, _ ->
                db.deleteStatus(status.id)
                val pos = list.indexOf(status)
                list.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                toast("Status deleted")
            }
            .setNegativeButton("No", null)
            .show()
    }

    // ✅ Helper toast
    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}





