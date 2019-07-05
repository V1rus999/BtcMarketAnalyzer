package markets.crypto_exchanges.binance

import markets.Ticker
import markets.crypto_exchanges.Exchange
import helpers.Failure
import helpers.Result
import network.RetrofitFinMarketApi
import helpers.Success
import okhttp3.HttpUrl
import org.pmw.tinylog.Logger
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class BinanceExchange : Exchange {

    companion object {
        const val exchangeName = "Binance"
        const val exchangePair = "tBTCUSD"
    }

    private val requestUrl = HttpUrl.parse("https://api.binance.com/")
    private val retrofit =
        Retrofit.Builder().baseUrl(requestUrl!!)
            .addConverterFactory(GsonConverterFactory.create()).build()
    private val btcApi = retrofit.create(RetrofitFinMarketApi::class.java)

    override fun exchangeName(): String = exchangeName

    override suspend fun getTicker(): Result<Ticker.BasicTicker, Exception> {
        return try {
            val tickers: Ticker.BasicTicker?
            val exchangeTicker = btcApi.getBinanceTicker()
            Logger.debug("Got data from $exchangeName $exchangeTicker")
            tickers = Ticker.BasicTicker(
                exchangeTicker.price,
                exchangePair,
                exchangeName()
            )

            Success(tickers)
        } catch (e: Exception) {
            Failure(e)
        }

    }
}