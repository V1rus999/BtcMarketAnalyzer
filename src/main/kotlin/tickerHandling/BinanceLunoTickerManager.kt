package tickerHandling

import markets.Ticker
import org.decimal4j.util.DoubleRounder
import persistance.ObjectWriter
import java.lang.Exception

/**
 * @Author: JohannesC
 * @Date: 2019-06-01, Sat
 **/
class BinanceLunoTickerManager(
    private val writer: ObjectWriter<Ticker.TrackedTicker>,
    private val queue: InMemoryQueue<Ticker.TrackedTicker>
) : TickerManager {

    private val QUEUE_FLUSH_LIMIT = 30

    override fun newTickerResultReceived(
        timeStamp: Long,
        firstTicker: Ticker.CryptoTicker?,
        secondTicker: Ticker.CryptoTicker?
    ) {
        //We will assume the first value is luno and second is binance
        if (firstTicker == null || secondTicker == null) {
            return
        }

        val trackedTicker =
            generateTrackedTickerForTimestamp(timeStamp, firstTicker, secondTicker, queue.peekLastValue())
        queue.enqueue(trackedTicker)
        println("Added item to queue")
        println(trackedTicker)

        try {
            println("Flushing queue")
            flushQueueToFile(queue)
        } catch (e: Exception) {
            println("Issue flushing queue")
        }
    }

    private fun flushQueueToFile(queue: InMemoryQueue<Ticker.TrackedTicker>) {
        if (queue.checkSize() > QUEUE_FLUSH_LIMIT) {
            val item = queue.dequeue()
            if (item != null) {
                writer.writeObject(item)
            }
        }
    }

    private fun generateTrackedTickerForTimestamp(
        timeStamp: Long,
        firstTicker: Ticker.CryptoTicker,
        secondTicker: Ticker.CryptoTicker,
        lastItemInQueue: Ticker.TrackedTicker?
    ): Ticker.TrackedTicker {
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

    private fun getPriceIncreasePercentage(lastValue: Ticker.CryptoTicker?, ticker: Ticker.CryptoTicker): Double =
        DoubleRounder.round(PriceCalculations.getPriceChangePercentage(lastValue?.price ?: 0.0, ticker.price), 4)

}