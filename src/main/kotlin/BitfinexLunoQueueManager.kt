import markets.Ticker
import tickerHandling.InMemoryQueue
import tickerHandling.QueueManager

/**
 * @Author: JohannesC
 * @Date: 2019-06-01, Sat
 **/
class BitfinexLunoQueueManager : QueueManager {

    private val queue = InMemoryQueue()

    override fun newTickerResultReceived(
        timeStamp: Long,
        firstTicker: Ticker.CryptoTicker?,
        secondTicker: Ticker.CryptoTicker?
    ) {
        //We will assume the first value is luno and second is bitfinex
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
                lastItemInQueue.cryptoPair.first.ticker,
                firstTicker
            ), firstTicker
        )

        val bitfinexTicker = Ticker.TickerTypes.BitfinexTicker(
            getPriceIncreasePercentage(
                lastItemInQueue.cryptoPair.first.ticker,
                secondTicker
            ), secondTicker
        )

        return Ticker.TrackedTicker(timeStamp, Pair(lunoTicker, bitfinexTicker))
    }

    private fun getPriceIncreasePercentage(lastValue: Ticker.CryptoTicker?, ticker: Ticker.CryptoTicker): Double {
        if (lastValue == null) return 0.0
        val lastPrice = lastValue.price
        val newPrice = ticker.price
        return (newPrice - lastPrice) / lastPrice * 100
    }
}