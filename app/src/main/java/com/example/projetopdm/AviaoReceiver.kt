package com.example.projetopdm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AviaoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Mexeu no modo avião", Toast.LENGTH_SHORT).show()
    }
}