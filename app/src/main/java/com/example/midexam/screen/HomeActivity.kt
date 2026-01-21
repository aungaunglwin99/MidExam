package com.example.midexam.screen

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.midexam.R
import com.example.midexam.adapter.StatusAdapter
import com.example.midexam.data.DatabaseHelper
import com.example.midexam.databinding.ActivityHomeBinding
import com.example.midexam.model.StatusModel
import com.example.midexam.util.showToast

class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private lateinit var db: DatabaseHelper
    lateinit var prefs: SharedPreferences
    private var userId = -1
    private lateinit var adapter: StatusAdapter
    private lateinit var list: MutableList<StatusModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        setToolbar()
        setPrefs()
        setPostAdapter()
        binding.btnUpload.setOnClickListener {
            uploadPost()
        }

    }


    private fun setToolbar() = with(binding) {
        setSupportActionBar(toolbar.toolbar)
        supportActionBar?.title = "Home"
    }

    private fun setPrefs() {
        prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        userId = intent.getIntExtra("USER_ID", prefs.getInt("USER_ID", -1))

        if (userId == -1) {
            // No logged-in user, return to login
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
            return
        }
    }

    private fun setPostAdapter() = with(binding) {
        list = db.getStatuses(userId)
        adapter = StatusAdapter(
            list,
            onEditClick = { status -> showEditDialog(status) },
            onDeleteClick = { status -> deleteStatus(status) }
        )
        rvStatus.adapter = adapter
    }

    private fun uploadPost() = with(binding) {
        val text = etStatus.text.toString().trim()
        if (text.isEmpty()) {
            showToast("Enter status")
        } else {
            val newStatus = db.addStatus(userId, text)
            list.add(newStatus)
            adapter.notifyItemInserted(list.size - 1)
            rvStatus.scrollToPosition(list.size - 1)
            etStatus.setText("")
            showToast("You Post A Status")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        showConfirmDialog(
            title = "Logout",
            message = "Are you sure you want to logout?",
            positiveText = "Yes"
        ) {
            getSharedPreferences("login_prefs", MODE_PRIVATE)
                .edit {
                    clear()
                }

            startActivity(
                Intent(this, LogInActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
    }

    private fun showEditDialog(statusModel: StatusModel) {
        val et = EditText(this).apply {
            setText(statusModel.text)
        }

        showConfirmDialog(
            title = "Edit Status",
            view = et,
            positiveText = "Save"
        ) {
            val newText = et.text.toString().trim()
            if (newText.isNotEmpty()) {
                db.updateStatus(statusModel.id, newText)
                statusModel.text = newText
                adapter.notifyDataSetChanged()
                showToast("Status updated")
            }
        }
    }


    private fun deleteStatus(statusModel: StatusModel) {
        showConfirmDialog(
            title = "Delete Status",
            message = "Are you sure you want to delete this status?",
            positiveText = "Yes",
            negativeText = "No"
        ) {
            db.deleteStatus(statusModel.id)
            val pos = list.indexOf(statusModel)
            list.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            showToast("Status deleted")
        }
    }


    private fun showConfirmDialog(
        title: String,
        message: String? = null,
        view: View? = null,
        positiveText: String,
        negativeText: String = "Cancel",
        onPositive: () -> Unit
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            message?.let { setMessage(it) }
            view?.let { setView(it) }
            setPositiveButton(positiveText) { _, _ -> onPositive() }
            setNegativeButton(negativeText, null)
        }.show()
    }

}





