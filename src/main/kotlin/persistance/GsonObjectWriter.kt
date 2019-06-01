package persistance

import com.google.gson.Gson
import java.io.File

/**
 * @Author: johannesC
 * @Date: 2019-06-01, Sat
 **/
class GsonObjectWriter<T>(private val fileRetriever: () -> File) : ObjectWriter<T> {

    private val gson = Gson()

    override fun writeObject(value: T) {
        val gsonedString = gson.toJson(value)
        val file = fileRetriever.invoke()
        file.appendText(gsonedString)
        file.appendText("\n")
    }
}