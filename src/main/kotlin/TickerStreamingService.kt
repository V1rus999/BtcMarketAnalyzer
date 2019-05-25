import markets.Ticker
import markets.crypto_exchanges.CryptoExchange
import network.Failure
import network.Success
import java.awt.Robot
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.awt.MouseInfo


/**
 * Created by johannesC on 2017/09/03.
 */
class TickerStreamingService(
    private val cryptoExchanges: List<CryptoExchange>
) {

    private var scheduler: ScheduledExecutorService? = null
    private val robot = Robot()

    fun startDownloadingTickerData() {
        scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduler?.scheduleAtFixedRate({

            val tickers = arrayListOf<Ticker.CryptoTicker>()
            for (exchange in cryptoExchanges) {
                println("Checking ${exchange.exchangeName()}")
                when (val result = exchange.getTicker()) {
                    is Success -> {
                        println("Got results from ${exchange.exchangeName()}")
                        println(result.value)
                        tickers.add(result.value)}
                    is Failure -> println(result.reason)
                }
            }
            val pObj = MouseInfo.getPointerInfo().location
            robot.mouseMove(pObj.x + 1, pObj.y + 1)
            robot.mouseMove(pObj.x - 1, pObj.y - 1)

            println("Got the following tickers: ")
            println(tickers)
            println("Rescheduling for 15 minutes")

        }, 0, 15, TimeUnit.MINUTES)
    }

    fun stopDownloadingTickerData() {
        scheduler?.shutdown()
    }
}