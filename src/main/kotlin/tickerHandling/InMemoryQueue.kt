package tickerHandling

import markets.Ticker
import java.util.*

/**
 * @Author: JohannesC
 * @Date: 2019-06-01, Sat
 **/
class InMemoryQueue {

    private val queue: Deque<Ticker.TrackedTicker> = LinkedList()

    fun enqueue(ticker: Ticker.TrackedTicker) {
        queue.add(ticker)
    }

    fun dequeue(ticker: Ticker.TrackedTicker) = queue.poll()

    fun peekLastValue() = queue.peekLast()

    fun checkSize() = queue.size
}