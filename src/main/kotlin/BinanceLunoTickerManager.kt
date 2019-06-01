import markets.Ticker
import tickerHandling.InMemoryQueue
import tickerHandling.TickerManager

/**
 * @Author: JohannesC
 * @Date: 2019-06-01, Sat
 **/
class BinanceLunoTickerManager : TickerManager {

    private val queue = InMemoryQueue()

    override fun newTickerResultReceived(
        timeStamp: Long,
        firstTicker: Ticker.CryptoTicker?,
        secondTicker: Ticker.CryptoTicker?
    ) {
        //We will assume the first value is luno and second is binance
        if (firstTicker == null || secondTicker == null) {
            return
        }

        val trackedTicker = generateTrackedTickerForTimestamp(timeStamp, firstTicker, secondTicker)
        queue.enqueue(trackedTicker)
        println("Added item to queue")
        println(trackedTicker)
    }

    private fun generateTrackedTickerForTimestamp(
        timeStamp: Long,
        firstTicker: Ticker.CryptoTicker,
        secondTicker: Ticker.CryptoTicker
    ): Ticker.TrackedTicker {
        val lastItemInQueue = queue.peekLastValue()
        val lunoTicker = Ticker.TickerTypes.LunoTicker(
            getPriceIncreasePercentage(
                lastItemInQueue?.cryptoPair?.first?.ticker,
                firstTicker
            ), firstTicker
        )

        val binanceTicker = Ticker.TickerTypes.BinanceTicker(
            getPriceIncreasePercentage(
                lastItemInQueue?.cryptoPair?.second?.ticker,
                secondTicker
            ), secondTicker
        )

        return Ticker.TrackedTicker(timeStamp, Pair(lunoTicker, binanceTicker))
    }

    private fun getPriceIncreasePercentage(lastValue: Ticker.CryptoTicker?, ticker: Ticker.CryptoTicker): Double {
        if (lastValue == null) return 0.0
        val lastPrice = lastValue.price
        val newPrice = ticker.price
        return ((newPrice - lastPrice) / lastPrice) * 100
    }
}