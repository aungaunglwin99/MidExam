package com.example.loginsystem

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.midexam.Status

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "LoginDB", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE COLLATE NOCASE,
                password TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE status (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                text TEXT,
                created_at INTEGER
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS status")
        onCreate(db)
    }

    // ----------------- USER -----------------
    fun register(username: String, password: String): Boolean {
        val cv = ContentValues()
        cv.put("username", username.trim())
        cv.put("password", password)
        return writableDatabase.insert("users", null, cv) != -1L
    }

    fun login(username: String, password: String): Int {
        val c = readableDatabase.rawQuery(
            "SELECT id FROM users WHERE username=? AND password=?",
            arrayOf(username.trim(), password)
        )
        val id = if (c.moveToFirst()) c.getInt(0) else -1
        c.close()
        return id
    }

    // ----------------- STATUS -----------------
    fun addStatus(userId: Int, text: String): Status {
        val cv = ContentValues()
        cv.put("user_id", userId)
        cv.put("text", text)
        cv.put("created_at", System.currentTimeMillis())
        val id = writableDatabase.insert("status", null, cv).toInt()
        return Status(id, text)
    }

    fun updateStatus(statusId: Int, newText: String) {
        writableDatabase.execSQL(
            "UPDATE status SET text=? WHERE id=?",
            arrayOf(newText, statusId)
        )
    }

    fun deleteStatus(statusId: Int) {
        writableDatabase.execSQL(
            "DELETE FROM status WHERE id=?",
            arrayOf(statusId)
        )
    }

    fun getStatuses(userId: Int): MutableList<Status> {
        val list = mutableListOf<Status>()
        val c = readableDatabase.rawQuery(
            "SELECT id, text FROM status WHERE user_id=? ORDER BY created_at ASC",
            arrayOf(userId.toString())
        )
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val text = c.getString(1)
            list.add(Status(id, text))
        }
        c.close()
        return list
    }
}


