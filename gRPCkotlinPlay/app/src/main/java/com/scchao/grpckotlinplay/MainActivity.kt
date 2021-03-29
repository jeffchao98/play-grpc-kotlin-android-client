package com.scchao.grpckotlinplay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {
    private val logger = Logger.getLogger(this.javaClass.name)
    private fun channel(): ManagedChannel {
        val url = URL("http://192.168.3.8:50051")
//        val url = URL(resources.getString(R.string.server_url))
        val port = if (url.port == -1) url.defaultPort else url.port

        logger.info("Connecting to ${url.host}:$port")

        val builder = ManagedChannelBuilder.forAddress(url.host, port)
        if (url.protocol == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }

        return builder.executor(Dispatchers.Default.asExecutor()).build()
    }

    // lazy otherwise resources is null
    private val greeter by lazy { GreeterGrpcKt.GreeterCoroutineStub(channel()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun sendReq() = runBlocking {
            try {
                val request = HelloRequest.newBuilder().setName("bland tester").build()
                val response = greeter.reSayHello(request)
                Log.i("[gRPC play] Response:", response.message?: "")
//                responseText.text = response.message
            } catch (e: Exception) {
                Log.i("[gRPC play] Error", e.message?: "")
//                responseText.text = e.message
                e.printStackTrace()
            }
        }

        sendReq()
    }
}