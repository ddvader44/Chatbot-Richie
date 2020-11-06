package com.ddvader44.chatbotrichie.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddvader44.chatbotrichie.R
import com.ddvader44.chatbotrichie.data.Message
import com.ddvader44.chatbotrichie.utils.BotResponses
import com.ddvader44.chatbotrichie.utils.Constants.OPEN_GOOGLE
import com.ddvader44.chatbotrichie.utils.Constants.OPEN_SEARCH
import com.ddvader44.chatbotrichie.utils.Constants.RECEIVE_ID
import com.ddvader44.chatbotrichie.utils.Constants.SEND_ID
import com.ddvader44.chatbotrichie.utils.Time
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter : MessageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView()

        clickEvents()

        customMessage("Hello, This is Richie! How may I help?")

    }

    private fun customMessage(message : String) {
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                val timeStamp = Time.timeStamp()
                adapter.insertMessage(Message(message, RECEIVE_ID,timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount-1)
            }
        }
    }

    private fun clickEvents() {

        btn_send.setOnClickListener {
            sendMessage()
        }
        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount-1)
                }
            }
        }
    }

    private fun sendMessage() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()
        if(message.isNotEmpty()){
            et_message.setText("")
            adapter.insertMessage(Message(message,SEND_ID,timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount-1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timestamp = Time.timeStamp()
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val response = BotResponses.basicResponses(message)
                adapter.insertMessage(Message(response, RECEIVE_ID,timestamp))
                rv_messages.scrollToPosition(adapter.itemCount-1)
                when(response) {
                    OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfter("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }
                }
        }
        }
    }

    private fun recyclerView() {
        adapter = MessageAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)
    }

    // keep chat at bottom of screen

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount-1)
            }
        }
    }
}