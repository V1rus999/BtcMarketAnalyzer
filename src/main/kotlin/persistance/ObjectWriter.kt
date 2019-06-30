package persistance

import java.lang.Exception
import helpers.Result

/**
 * @Author: johannesC
 * @Date: 2019-06-01, Sat
 **/
interface ObjectWriter<T> {

    fun writeObject(value: T) : Result<Unit, Exception>
}