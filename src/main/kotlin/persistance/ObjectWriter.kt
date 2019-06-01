package persistance

/**
 * @Author: johannesC
 * @Date: 2019-06-01, Sat
 **/
interface ObjectWriter<T> {

    fun writeObject(value: T)
}