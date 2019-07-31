import helpers.OutputFileHandler
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

    private const val isDebugRun = true

    @JvmStatic
    fun main(args: Array<String>) {
        println("Starting...")
        println("Press 1 and enter to quit")

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)
        setupLogging(date)

        val exchangeFactory = ExchangeFactory()
        val outputFileHandler =
            OutputFileHandler("${LunoExchange.exchangeName}${BinanceExchange.exchangeName}", isDebugRun)
        val lunoBinanceService: TickerStream = BulkTickerStream(
            listOf(
                exchangeFactory.getExchange(LunoExchange.exchangeName),
                exchangeFactory.getExchange(BinanceExchange.exchangeName)
            ),
            InMemoryQueue(),
            GsonObjectWriter { outputFileHandler.retrieveOutputFile() }
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

    private fun setupLogging(date: String) {
        val logLocation = if (isDebugRun) "debuglogging/${date}log.txt" else "logging/${date}log.txt"
        try {
            Configurator.defaultConfig().writer(FileWriter(logLocation)).addWriter(ConsoleWriter()).activate()
            Logger.info("Successfully set up logging at $logLocation")
        } catch (e: Exception) {
            println("Error setting up logging $e")
        }
    }
}