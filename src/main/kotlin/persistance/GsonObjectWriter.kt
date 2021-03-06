package persistance

import com.google.gson.Gson
import helpers.Failure
import helpers.Result
import helpers.Success
import java.io.File
import kotlin.Exception

/**
 * @Author: johannesC
 * @Date: 2019-06-01, Sat
 **/
class GsonObjectWriter<T>(private val fileRetriever: () -> File) :
    ObjectWriter<T> {

    private val gson = Gson()

    override suspend fun writeObject(value: T): Result<Unit, Exception> {
        return try {
            val gsonedString = gson.toJson(value)
            val file = fileRetriever.invoke()
            file.appendText(gsonedString)
            file.appendText("\n")
            Success(Unit)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}