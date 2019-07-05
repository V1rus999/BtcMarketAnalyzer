package streaming

import helpers.*
import markets.Ticker
import markets.crypto_exchanges.Exchange
import org.pmw.tinylog.Logger
import persistance.ObjectWriter
import tickerHandling.InMemoryQueue
import tickerHandling.TickerTransformer
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
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")

    override fun startDownloadingTickerData() {
        timer = fixedRateTimer("PairTickerStreamingServiceThread", false, 0L, period = time) {
            execute {
                val timeStamp = getTimeStampNow()
                Logger.info("Starting ticker download for timestamp $timeStamp")
                val networkResults = ior { getTickersFromExchanges() }
                val outputTicker = generateTickerForOutput(timeStamp, networkResults)
                enqueueNewOutputTicker(outputTicker)
                ior { writeTickerToFile(outputTicker) }
                Logger.info("Done for timestamp $timeStamp, rescheduling next run for $time milliseconds ")
            }
        }
    }

    private fun generateTickerForOutput(
        timeStamp: String,
        networkResults: List<Ticker.BasicTicker>
    ): Ticker.OutputTicker =
        TickerTransformer.transformTickers(timeStamp, networkResults, queue.pollLastValue())

    private fun enqueueNewOutputTicker(ticker: Ticker.OutputTicker) {
        queue.enqueue(ticker)
    }

    private suspend fun writeTickerToFile(ticker: Ticker.OutputTicker) {
        when (val result = writer.writeObject(ticker)) {
            is Failure -> Logger.error(result.reason, "Issue flushing queue")
        }
    }

    private suspend fun getTickersFromExchanges(): List<Ticker.BasicTicker> = exchanges.mapNotNull {
        getExchangeResult(it)
    }

    private suspend fun getExchangeResult(exchange: Exchange): Ticker.BasicTicker? =
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
        return current.format(formatter)
    }

    override fun stopDownloadingTickerData() {
        timer?.cancel()
    }

    private fun Long.toMinutes(): Long = 1000 * this * 60
}