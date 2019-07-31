package helpers

import org.pmw.tinylog.Logger
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @Author: johannesC
 * @Date: 2019-07-07, Sun
 **/
class OutputFileHandler(private val additionalFileNameInfo: String, private val isDebugRun: Boolean = false) {

    fun retrieveOutputFile(): File {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)

        val outputFile =
            if (isDebugRun) File("debugtickerData/${date}TickerFor$additionalFileNameInfo.json")
            else File("tickerData/${date}TickerFor$additionalFileNameInfo.json")
        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdir()
        }

        if (!outputFile.exists())
            Logger.info("Created output file at ${outputFile.absolutePath}")
        return outputFile
    }
}