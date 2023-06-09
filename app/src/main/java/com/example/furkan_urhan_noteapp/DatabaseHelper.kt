package com.example.furkan_urhan_noteapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "note_app.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "note_app"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DETAIL = "detail"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSQL = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_DETAIL TEXT, " +
                "$COLUMN_DATE TEXT)"
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addNote(title: String, detail: String, date: String): Long {
        val contentValues = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_DATE, date)
        }

        val db = writableDatabase
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun getAllNotes(): ArrayList<String> {
        val itemList = ArrayList<String>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))
                val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                itemList.add("$title - $detail - $date")
            } while (cursor.moveToNext())
        }

        cursor.close()
        return itemList
    }

    fun deleteNote(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getNoteId(item: String): Int {
        val parts = item.split(" - ")
        val title = parts[0]
        val detail = parts[1]
        val date = parts[2]

        val selectQuery = "SELECT $COLUMN_ID FROM $TABLE_NAME WHERE $COLUMN_TITLE = ? AND $COLUMN_DETAIL = ? AND $COLUMN_DATE = ?"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(title, detail, date))

        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
        }

        cursor.close()
        return id
    }
}
