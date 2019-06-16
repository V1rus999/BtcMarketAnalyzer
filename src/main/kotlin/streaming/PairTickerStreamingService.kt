package streaming

import markets.Ticker
import markets.crypto_exchanges.CryptoExchange
import network.Failure
import network.Success
import org.pmw.tinylog.Logger
import tickerHandling.TickerManager
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * Created by johannesC on 2017/09/03.
 */
class PairTickerStreamingService(
    private val firstExchange: CryptoExchange,
    private val secondExchange: CryptoExchange,
    private val tickerManager: TickerManager
) {

    private val time: Long = 1L.toMinutes()
    private var timer : Timer? = null

    fun startDownloadingTickerData() {
        timer = fixedRateTimer("PairTickerStreamingServiceThread", false, 0L, period = time){
            val timeStamp = getTimeStampNow()
            Logger.info("Starting ticker download for timestamp $timeStamp")

            val firstResult = getExchangeResult(firstExchange)
            val secondResult = getExchangeResult(secondExchange)
            tickerManager.newTickerResultReceived(timeStamp, firstResult, secondResult)
            Logger.info("Done for timestamp $timeStamp, rescheduling next run for $time milliseconds ")
        }
    }

    private fun getExchangeResult(exchange: CryptoExchange): Ticker.CryptoTicker? =
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

    fun stopDownloadingTickerData() {
        timer?.cancel()
    }

    private fun Long.toMinutes() : Long = 1000 * this * 60
}