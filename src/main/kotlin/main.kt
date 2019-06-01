import markets.ExchangeFactory
import markets.crypto_exchanges.binance.BinanceExchange
import markets.crypto_exchanges.luno.LunoExchange
import streaming.PairTickerStreamingService
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args: Array<String>) {
    println("Starting...")

    val exchangeFactory = ExchangeFactory()
    val service = PairTickerStreamingService(
        exchangeFactory.getExchange(LunoExchange.exchangeName),
        exchangeFactory.getExchange(BinanceExchange.exchangeName),
        BinanceLunoQueueManager()
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