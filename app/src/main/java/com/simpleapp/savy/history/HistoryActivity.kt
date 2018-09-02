package com.simpleapp.savy.history

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.simpleapp.savy.DatePickerFragment
import com.simpleapp.savy.MainActivity
import com.simpleapp.savy.model.record.DailyRecord
import com.simpleapp.savy.R
import com.simpleapp.savy.model.Date
import com.simpleapp.savy.model.Wallet
import com.simpleapp.savy.recordedit.RecordEditActivity
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    private var currentDatestamp: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        currentDatestamp = this.intent.getIntExtra("datestamp", -1)
        setResult(MainActivity.RESULT_HISTORY, getResult())
        setupDate(Date(currentDatestamp))
        setupClickListener()
    }

    private fun setupDate(date: Date) {
        val dateText = date.getDayName() + ", " +
                date.day.toString() + " " +
                Date.MonthName(date.month) + " " +
                date.year.toString()
        tvDate.setText(dateText)
    }

    private fun setupClickListener() {
        tvDate.setOnClickListener{_ ->
            val fragment = DatePickerFragment.Builder(Date(currentDatestamp), object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    changeDate(Date(dayOfMonth, month, year))
                }
            })
            fragment.show(supportFragmentManager, "date")
        }
        btnPreviousDay.setOnClickListener{ _ ->
            changeDate(Date(currentDatestamp).yesterday())
        }
        btnNextDay.setOnClickListener{ _ ->
            changeDate(Date(currentDatestamp).tommorow())
            Log.d("TEST", "tomorrow")
        }
    }

    private fun changeDate(date: Date) {
        currentDatestamp = date.toDatestamp()
        setupDate(Date(currentDatestamp))
        setResult(MainActivity.RESULT_HISTORY, getResult())
        showHistory(currentDatestamp!!)
    }

    private fun getResult() : Intent {
        val intent = Intent()
        intent.putExtra("datestamp", currentDatestamp)
        return intent
    }

    private fun showHistory(datestamp: Int) {
        if (datestamp == -1) {
            Toast.makeText(this, "Cannot get datestamp", Toast.LENGTH_SHORT).show()
        } else {
            val records = Wallet.getWallet("My Wallet").getRecords(datestamp)
            rvHistory.layoutManager = LinearLayoutManager(this)
            rvHistory.adapter = HistoryAdapter(records.recordList, this, object : HistoryItemClickListener {
                override fun onClick(id: Long) {
                    val intent = Intent(applicationContext, RecordEditActivity::class.java)
                    intent.putExtra("id", id)
                    startActivity(intent)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        showHistory(currentDatestamp!!)
    }
}
