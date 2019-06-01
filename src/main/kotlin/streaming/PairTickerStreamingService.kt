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
        }, 0, 1, TimeUnit.MINUTES)
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