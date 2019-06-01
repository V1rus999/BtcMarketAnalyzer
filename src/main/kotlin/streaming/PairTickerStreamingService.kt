package streaming

import markets.Ticker
import markets.crypto_exchanges.CryptoExchange
import network.Failure
import network.Success
import org.pmw.tinylog.Logger
import tickerHandling.TickerManager
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
    private val tickerManager: TickerManager
) {

    private var scheduler: ScheduledExecutorService? = null
    private val robot = Robot()
    private val time: Long = 30
    private val timeUnit = TimeUnit.SECONDS

    fun startDownloadingTickerData(withMouseMovementBot: Boolean = false) {
        scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduler?.scheduleAtFixedRate({
            val timeStamp = System.currentTimeMillis()
            val firstResult = getExchangeResult(firstExchange)
            val secondResult = getExchangeResult(secondExchange)
            tickerManager.newTickerResultReceived(timeStamp, firstResult, secondResult)
            Logger.info("Done for $timeStamp, rescheduling next run for $time $timeUnit ")

            if (withMouseMovementBot) {
                val pObj = MouseInfo.getPointerInfo().location
                robot.mouseMove(pObj.x + 1, pObj.y + 1)
                robot.mouseMove(pObj.x - 1, pObj.y - 1)
            }
        }, 0, time, timeUnit)
    }

    private fun getExchangeResult(exchange: CryptoExchange): Ticker.CryptoTicker? =
        when (val result = exchange.getTicker()) {
            is Success -> {
                Logger.info("Got results from ${exchange.exchangeName()}", result.value)
                result.value
            }
            is Failure -> {
                Logger.error(result.reason, "Failed results from ${exchange.exchangeName()}")
                null
            }
        }

    fun stopDownloadingTickerData() {
        scheduler?.shutdown()
    }
}