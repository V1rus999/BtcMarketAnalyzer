import markets.ExchangeFactory
import markets.crypto_exchanges.binance.BinanceExchange
import markets.crypto_exchanges.luno.LunoExchange
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Logger
import org.pmw.tinylog.writers.ConsoleWriter
import org.pmw.tinylog.writers.FileWriter
import persistance.GsonObjectWriter
import streaming.BulkTickerStream
import streaming.TickerStream
import tickerHandling.InMemoryQueue
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Starting...")
        println("Press 1 and enter to quit")

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)
        setupLogging(date)

        val exchangeFactory = ExchangeFactory()
        val lunoBinanceService: TickerStream = BulkTickerStream(
            listOf(
                exchangeFactory.getExchange(LunoExchange.exchangeName),
                exchangeFactory.getExchange(BinanceExchange.exchangeName)
            ),
            InMemoryQueue(),
            GsonObjectWriter(::retrieveOutputFile, "${LunoExchange.exchangeName}${BinanceExchange.exchangeName}")
        )
        lunoBinanceService.startDownloadingTickerData()

        val `in` = BufferedReader(InputStreamReader(System.`in`))
        val a = `in`.readLine()
        if (a == "1") {
            Logger.info("Stopping Service...")
            lunoBinanceService.stopDownloadingTickerData()
            Logger.info("Quitting...")
        }
    }

    private fun retrieveOutputFile(additionalFileNameInfo: String): File {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)

        val outputFile = File("tickerData/${date}TickerFor$additionalFileNameInfo.json")
        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdir()
        }

        if (!outputFile.exists())
            Logger.info("Created output file at ${outputFile.absolutePath}")
        return outputFile
    }

    private fun setupLogging(date: String) {
        val logLocation = "logging/${date}log.txt"
        try {
            Configurator.defaultConfig().writer(FileWriter(logLocation)).addWriter(ConsoleWriter()).activate()
            Logger.info("Successfully set up logging at $logLocation")
        } catch (e: Exception) {
            println("Error setting up logging $e")
        }
    }
}