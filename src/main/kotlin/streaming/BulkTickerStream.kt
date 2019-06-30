package streaming

import markets.Ticker
import markets.crypto_exchanges.Exchange
import network.Failure
import network.Success
import org.pmw.tinylog.Logger
import persistance.ObjectWriter
import tickerHandling.InMemoryQueue
import tickerHandling.TickerTransformer
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * @Author: johannesC
 * @Date: 2019-06-30, Sun
 **/
class BulkTickerStream(
    private val exchanges: List<Exchange>,
    private val queue: InMemoryQueue<Ticker.OutputTicker>,
    private val writer: ObjectWriter<Ticker.OutputTicker>
) : TickerStream {

    private val time: Long = 1L.toMinutes()
    private var timer: Timer? = null
    private val QUEUE_FLUSH_LIMIT = 6

    override fun startDownloadingTickerData() {
        timer = fixedRateTimer("PairTickerStreamingServiceThread", false, 0L, period = time) {
            val timeStamp = getTimeStampNow()
            Logger.info("Starting ticker download for timestamp $timeStamp")

            val results: List<Ticker.BasicTicker> = exchanges.mapNotNull {
                getExchangeResult(it)
            }

            val outputTicker = TickerTransformer.transformTickers(timeStamp, results, queue.peekLastValue())
            queue.enqueue(outputTicker)

            try {
                Logger.info("Flushing queue QUEUE_FLUSH_LIMIT $QUEUE_FLUSH_LIMIT")
                flushQueueToFile(queue)
            } catch (e: Exception) {
                Logger.error(e, "Issue flushing queue")
            }

            Logger.info("Done for timestamp $timeStamp, rescheduling next run for $time milliseconds ")
        }
    }

    private fun flushQueueToFile(queue: InMemoryQueue<Ticker.OutputTicker>) {
        val item = queue.peekLastValue()

        if (queue.checkSize() > QUEUE_FLUSH_LIMIT) {
            Logger.info("Queue size above $QUEUE_FLUSH_LIMIT. Flushing item now")
            queue.dequeue()
        }

        if (item != null) {
            Logger.info("Item found in queue. Writing to file")
            writer.writeObject(item)
        } else {
            Logger.info("No item found in queue")
        }
    }

    private fun getExchangeResult(exchange: Exchange): Ticker.BasicTicker? =
        when (val result = exchange.getTicker()) {
            is Success -> {
                Logger.info("Got results from ${exchange.exchangeName()} ${result.value}")
                result.value
            }
            is Failure -> {
                Logger.error(result.reason, "Failed results from ${exchange.exchangeName()}")
                null
            }
        }

    private fun getTimeStampNow(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
        return current.format(formatter)
    }

    override fun stopDownloadingTickerData() {
        timer?.cancel()
    }

    private fun Long.toMinutes(): Long = 1000 * this * 60
}