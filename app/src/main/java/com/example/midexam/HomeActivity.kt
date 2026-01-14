package com.example.midexam

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsystem.DatabaseHelper
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper
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
        userId = intent.getIntExtra("USER_ID", -1)

        val etStatus = findViewById<EditText>(R.id.etStatus)
        val btnUpload = findViewById<MaterialButton>(R.id.btnUpload)
        val rv = findViewById<RecyclerView>(R.id.rvStatus)

        // Load statuses
        list = db.getStatuses(userId)

        adapter = StatusAdapter(
            list,
            onEditClick = { status -> showEditDialog(status) },
            onDeleteClick = { status -> deleteStatus(status) }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

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
            }
        }
    }

    // ✅ ADD THIS: show logout button in toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    // ✅ ADD THIS: handle logout click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ✅ ADD THIS: logout confirmation dialog
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, LogInActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

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

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}




