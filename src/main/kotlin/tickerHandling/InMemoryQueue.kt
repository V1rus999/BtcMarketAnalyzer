package tickerHandling

import java.util.*

/**
 * @Author: JohannesC
 * @Date: 2019-06-01, Sat
 **/
class InMemoryQueue<T> {

    private val queue: Deque<T> = LinkedList()

    fun enqueue(item: T) {
        queue.add(item)
    }

    fun dequeue() : T? = queue.poll()

    fun peekLastValue() : T? = queue.peekLast()

    fun checkSize() = queue.size
}