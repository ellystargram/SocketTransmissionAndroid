package org.intec.finalclass

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket

class MainActivity : AppCompatActivity() {
    private var clientLog: TextView? = null
    private var serverLog: TextView? = null
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val editText = findViewById<EditText>(R.id.field)
        val sendButton = findViewById<Button>(R.id.transmit)
        val activateButton = findViewById<Button>(R.id.activate)
        clientLog = findViewById<TextView>(R.id.client_log)
        serverLog = findViewById<TextView>(R.id.server_log)

        sendButton.setOnClickListener {
            val string = editText.text.toString()
            Thread {
                send(string)
            }.start()
        }
        activateButton.setOnClickListener {
            Thread {
                activateServer()
            }.start()
        }
    }

    private fun send(string: String) {
        try {
            val portNumber = 6974
            val socket = Socket("localhost", portNumber)
            printClientLog("Connected to socket")
            val outputStream = ObjectOutputStream(socket.getOutputStream())
            outputStream.writeObject(string)
            outputStream.flush()
            printClientLog("Sent message: $string")

            val inputStream = ObjectInputStream(socket.getInputStream())
            printClientLog("Received message: ${inputStream.readObject()}")
            socket.close()
        } catch (e: Exception) {
            printClientLog("Error: ${e.message}")
        }
    }

    private fun activateServer() {
        try {
            val portNumber = 6974
            val server = ServerSocket(portNumber)
            printClientLog("Server activated")

            while (true) {
                val socket = server.accept()
                val clientHost = socket.localAddress
                val clientPort = socket.port
                printServerLog("Connected to client $clientHost:$clientPort")

                val inputStream = ObjectInputStream(socket.getInputStream())
                val string = inputStream.readObject() as String
                printServerLog("Received message: $string")
                val outputStream = ObjectOutputStream(socket.getOutputStream())
                outputStream.writeObject("Received message: $string")
                outputStream.flush()
                printServerLog("Sent message: $string")
                socket.close()
            }
        } catch (e: Exception) {
            printServerLog("Error: ${e.message}")
        }
    }

    private fun printClientLog(string: String) {
        Log.d("MainActivity", string)
        handler.post {
            clientLog!!.append(string + "\n")
        }
    }

    private fun printServerLog(string: String) {
        Log.d("MainActivity", string)
        handler.post {
            serverLog!!.append(string + "\n")
        }
    }
}