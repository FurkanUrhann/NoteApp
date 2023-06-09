package com.example.furkan_urhan_noteapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDetail: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonDate: Button
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var itemList: ArrayList<String> = ArrayList()
    private val detailActivityRequestCode = 1
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDetail = findViewById(R.id.editTextDetail)
        buttonAdd = findViewById(R.id.buttonAdd)
        buttonDate = findViewById(R.id.buttonDate)
        listView = findViewById(R.id.listView)



        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter

        databaseHelper = DatabaseHelper(this)
        itemList = databaseHelper.getAllNotes()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter

        buttonAdd.setOnClickListener {
            val title = editTextTitle.text.toString()
            val detail = editTextDetail.text.toString()
            val item = "$title - $detail - $selectedDate"
            val id = databaseHelper.addNote(title, detail, selectedDate ?: "")
            if (id != -1L) {
                itemList.add(item)
                adapter.notifyDataSetChanged()
                clearInputFields()
            } else {
                Toast.makeText(this, "Failed to add note", Toast.LENGTH_SHORT).show()
            }
        }

        buttonDate.setOnClickListener {
            showDatePicker()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = itemList[position]
            val parts = selectedItem.split(" - ")
            val title = parts[0]
            val detail = parts[1]
            val date = parts[2]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("item", selectedItem)
            intent.putExtra("position", position)
            startActivityForResult(intent, detailActivityRequestCode)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectedDate = dateFormat.format(selectedCalendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == detailActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val position = data?.getIntExtra("position", -1)
            if (position != null && position != -1) {
                itemList.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        itemList = databaseHelper.getAllNotes()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter
    }

    private fun clearInputFields() {
        editTextTitle.text.clear()
        editTextDetail.text.clear()
    }

}