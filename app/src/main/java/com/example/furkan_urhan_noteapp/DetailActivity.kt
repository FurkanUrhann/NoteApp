package com.example.furkan_urhan_noteapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var noteId: Int = -1
    private lateinit var textViewTitle: TextView
    private lateinit var textViewDetail: TextView
    private lateinit var textViewDate: TextView
    private lateinit var buttonDelete: Button
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        databaseHelper = DatabaseHelper(this)
        textViewTitle = findViewById(R.id.textViewTitle)
        textViewDetail = findViewById(R.id.textViewDetail)
        textViewDate = findViewById(R.id.textViewDate)
        buttonDelete = findViewById(R.id.buttonDelete)

        val item = intent.getStringExtra("item")
        noteId = databaseHelper.getNoteId(item!!)

        val parts = item?.split(" - ")
        val title = parts?.get(0)
        val detail = parts?.get(1)
        val date = parts?.get(2)

        textViewTitle.text = title
        textViewDetail.text = detail
        textViewDate.text = date


        buttonDelete.setOnClickListener {
            if (noteId != -1) {
                databaseHelper.deleteNote(noteId)
                val intent = Intent()
                intent.putExtra("position", position)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
