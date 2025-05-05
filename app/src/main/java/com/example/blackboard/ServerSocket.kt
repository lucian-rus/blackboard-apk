package com.example.blackboard;

import java.io.OutputStream
import java.net.ServerSocket

// this has to be updated to inherit a proper class that should implement all the functions that we need

class ServerSocket {
    private val mPort = 8080

    /* @todo this should be updated to hold an proper class */
    private var coordinatesQueue = ArrayDeque<Pair<Int, Int>>()
    private var commandsQueue = ArrayDeque<Int>()

    fun init() {
        Thread(Runnable {
            val serverSocket: ServerSocket = ServerSocket(mPort)
            val socket = serverSocket.accept()
            val writer: OutputStream = socket.getOutputStream()
            writer.write(byteArrayOf(255.toByte(), 4, 0xA1.toByte(), 0xB1.toByte(), 0xA1.toByte(), 0xB1.toByte()))

            while (true) {
                /* at the moment, this takes precedence, will be edited out later */
                if (!coordinatesQueue.isEmpty()) {
                    val firstElement = coordinatesQueue.removeFirst();
                    val mX = firstElement.first;
                    val mY = firstElement.second;
                    /* send data */
                    writer.write(byteArrayOf(255.toByte(), 5, 0, (mX shr 8).toByte(), (mX and 255).toByte(), (mY shr 8).toByte(), (mY and 255).toByte()))
                    continue
                }

                if (!commandsQueue.isEmpty()) {
                    val firstElement = commandsQueue.removeFirst();
                    writer.write(byteArrayOf(255.toByte(), 1, firstElement.toByte()))
                }
            }
        }).start()
    }

    fun writeCoordinates(x: Int, y: Int) {
        coordinatesQueue.addLast(Pair(x, y))
    }

    fun writeChangeColor() {
        commandsQueue.addLast(1)
    }

    fun writeClearCanvas() {
        commandsQueue.addLast(2)
    }

    fun writeChangeLineWidth() {
        commandsQueue.addLast(3)
    }

    fun writeUndoLastAction() {
        commandsQueue.addLast(4);
    }

    fun writeRedoLastAction() {
        commandsQueue.addLast(5);
    }

    fun writeEraseSection() {
        commandsQueue.addLast(6);
    }
}
