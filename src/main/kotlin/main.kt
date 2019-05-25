import markets.ExchangeFactory
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args : Array<String>) {
    println("Starting...")

    val exchangeFactory = ExchangeFactory()
    val service = TickerStreamingService(exchangeFactory.getExchanges())
    println("Starting streaming service")
    service.startDownloadingTickerData()

    val `in` = BufferedReader(InputStreamReader(System.`in`))
    val a = `in`.readLine()
    if (a == "1") {
        println("Stopping Service...")
        service.stopDownloadingTickerData()
        println("Quitting...")
    }
}