package nl.mblankestijn.demo.sqs

import com.amazonaws.SdkClientException
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.SendMessageRequest
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.OVERFLOW
import java.nio.file.WatchEvent

fun main() {
    val sqs = AmazonSQSClientBuilder.defaultClient()

    val directory = Paths.get(System.getenv("SQS_INPUT_DIRECTORY") ?: ".")
    val rneQueue = System.getenv("SQS_QUEUE") ?: error("SQS_QUEUE is mandatory")
    val sender = SQSSender(sqs, rneQueue)
    println(directory.toAbsolutePath())

    val behaviour: Map<WatchEvent.Kind<*>, (WatchEvent<*>) -> Unit> = createBehaviour(directory, sender)
    val watcher = DirectoryWatchService(directory)
    watcher.watch(behaviour)
}

private fun createBehaviour(directory: Path, sender: SQSSender): Map<WatchEvent.Kind<*>, (WatchEvent<*>) -> Unit> = mapOf(
    OVERFLOW to { event -> println("${event.context()} overflow") },
    ENTRY_CREATE to { event ->
        val path = directory.resolve(event.context() as Path)
        println("${event.context()} was created")
        sender.send(String(Files.readAllBytes(path)))
        if (!path.toFile().delete()) {
            println("$path could not be deleted")
        }
    }
)

class DirectoryWatchService(private val directory: Path) {
    fun watch(m: Map<WatchEvent.Kind<*>, (WatchEvent<*>) -> Unit>) {
        val watchService = directory.fileSystem.newWatchService()
        directory.register(watchService, m.keys.toTypedArray())
        while (true) {
            val key = watchService.take()
            key.pollEvents()
                .forEach { event ->
                    when (val f = m[event.kind()]) {
                        null -> println("Ignoring event $event")
                        else -> f(event)
                    }
                }
            key.reset()
        }
    }
}

class SQSSender(private val sqs: AmazonSQS, private val queue: String) {
    fun send(content: String) {
        try {
            val result = sqs.sendMessage(SendMessageRequest(queue, content))
            println("Message send")
        } catch (e: SdkClientException) {
            println("Message failed")
            e.printStackTrace()
        }
    }
}