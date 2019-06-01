import markets.ExchangeFactory
import markets.Ticker
import markets.crypto_exchanges.binance.BinanceExchange
import markets.crypto_exchanges.luno.LunoExchange
import persistance.GsonObjectWriter
import streaming.PairTickerStreamingService
import tickerHandling.BinanceLunoTickerManager
import tickerHandling.InMemoryQueue
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    println("Starting...")

    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = current.format(formatter)

    val outputFile = File("TickerData/${date}Ticker.json")
    if (!outputFile.parentFile.exists()) {
        outputFile.parentFile.mkdir()
    }
    println("Created file at ${outputFile.absolutePath}")

    val exchangeFactory = ExchangeFactory()
    val service = PairTickerStreamingService(
        exchangeFactory.getExchange(LunoExchange.exchangeName),
        exchangeFactory.getExchange(BinanceExchange.exchangeName),
        BinanceLunoTickerManager(GsonObjectWriter(outputFile), InMemoryQueue())
    )
    println("Starting ${PairTickerStreamingService::class}")
    service.startDownloadingTickerData()

    val `in` = BufferedReader(InputStreamReader(System.`in`))
    val a = `in`.readLine()
    if (a == "1") {
        println("Stopping Service...")
        service.stopDownloadingTickerData()
        println("Quitting...")
    }
}