package com.github.qrscan

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.github.qrscanlib.Intents
import com.github.qrscanlib.QrScanActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnStart: Button
    private lateinit var tvContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart = findViewById(R.id.btn_start)
        tvContent = findViewById(R.id.tv_content)
        btnStart.setOnClickListener {
            val intent = Intent(this, QrScanActivity::class.java)
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100){
            if (resultCode == RESULT_OK){
                val result = data?.getStringExtra(Intents.QR_SCAN_RESULT)
                tvContent.text = result
            }
        }
    }
}