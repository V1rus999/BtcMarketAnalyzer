package persistance

import com.google.gson.Gson
import java.io.File

/**
 * @Author: johannesC
 * @Date: 2019-06-01, Sat
 **/
class GsonObjectWriter<T>(private val fileToWriteTo : File) : ObjectWriter<T> {

    private val gson = Gson()

    override fun writeObject(value: T) {
        val gsonedString = gson.toJson(value)
        fileToWriteTo.appendText(gsonedString)
        fileToWriteTo.appendText("\n")
    }
}