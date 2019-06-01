package streaming

import markets.Ticker
import markets.crypto_exchanges.CryptoExchange
import network.Failure
import network.Success
import tickerHandling.QueueManager
import java.awt.Robot
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.awt.MouseInfo

/**
 * Created by johannesC on 2017/09/03.
 */
class PairTickerStreamingService(
    private val firstExchange: CryptoExchange,
    private val secondExchange: CryptoExchange,
    private val queueManager: QueueManager
) {

    private var scheduler: ScheduledExecutorService? = null
    private val robot = Robot()
    private val time: Long = 30
    private val timeUnit = TimeUnit.SECONDS

    fun startDownloadingTickerData() {
        scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduler?.scheduleAtFixedRate({
            val timeStamp = System.currentTimeMillis()
            val firstResult = getExchangeResult(firstExchange)
            val secondResult = getExchangeResult(secondExchange)
            queueManager.newTickerResultReceived(timeStamp, firstResult, secondResult)

            val pObj = MouseInfo.getPointerInfo().location
            robot.mouseMove(pObj.x + 1, pObj.y + 1)
            robot.mouseMove(pObj.x - 1, pObj.y - 1)
            println("Done for $timeStamp, rescheduling next run for $time $timeUnit ")
        }, 0, time, timeUnit)
    }

    private fun getExchangeResult(exchange: CryptoExchange): Ticker.CryptoTicker? =
        when (val result = exchange.getTicker()) {
            is Success -> {
                println("Got results from ${exchange.exchangeName()}")
                println(result.value)
                result.value
            }
            is Failure -> {
                println("Failed results from ${exchange.exchangeName()}")
                println(result.reason)
                null
            }
        }

    fun stopDownloadingTickerData() {
        scheduler?.shutdown()
    }
}