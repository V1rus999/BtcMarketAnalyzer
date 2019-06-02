package tickerHandling

import markets.Ticker
import org.decimal4j.util.DoubleRounder
import org.pmw.tinylog.Logger
import persistance.ObjectWriter
import java.lang.Exception

/**
 * @Author: JohannesC
 * @Date: 2019-06-01, Sat
 **/
class LunoBinanceTickerManager(
    private val writer: ObjectWriter<Ticker.TrackedTicker>,
    private val queue: InMemoryQueue<Ticker.TrackedTicker>
) : TickerManager {

    private val QUEUE_FLUSH_LIMIT = 6

    override fun newTickerResultReceived(
        timeStamp: String,
        firstTicker: Ticker.CryptoTicker?,
        secondTicker: Ticker.CryptoTicker?
    ) {
        //We will assume the first value is luno and second is binance
        if (firstTicker == null || secondTicker == null) {
            Logger.error("Ticker values received were null firstTicker $firstTicker secondTicker $secondTicker")
            return
        }

        val trackedTicker =
            generateTrackedTickerForTimestamp(timeStamp, firstTicker, secondTicker, queue.peekLastValue())
        queue.enqueue(trackedTicker)
        Logger.info("Added item to queue", trackedTicker)

        try {
            Logger.info("Flushing queue QUEUE_FLUSH_LIMIT $QUEUE_FLUSH_LIMIT")
            flushQueueToFile(queue)
        } catch (e: Exception) {
            Logger.error(e, "Issue flushing queue")
        }
    }

    private fun flushQueueToFile(queue: InMemoryQueue<Ticker.TrackedTicker>) {
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

    private fun generateTrackedTickerForTimestamp(
        timeStamp: String,
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

    private fun getPriceIncreasePercentage(lastValue: Ticker.CryptoTicker?, ticker: Ticker.CryptoTicker): Double {
        val result = PriceCalculations.getPriceChangePercentage(lastValue?.price ?: 0.0, ticker.price)
        return DoubleRounder.round(result, 3)
    }
}