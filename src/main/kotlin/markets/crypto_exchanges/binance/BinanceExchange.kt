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
    }

    private val requestUrl = HttpUrl.parse("https://api.binance.com/")
    private val retrofit =
        Retrofit.Builder().baseUrl(requestUrl!!).addConverterFactory(GsonConverterFactory.create()).build()
    private val btcApi = retrofit.create(RetrofitFinMarketApi::class.java)

    override fun exchangeName(): String = exchangeName

    override fun getTicker(): Result<Ticker.BasicTicker, Exception> {
        val call = btcApi.getBinanceTicker()
        val tickers: Ticker.BasicTicker?

        try {
            val response = call.execute()
            if (response.isSuccessful) {
                Logger.debug("Got data from $exchangeName ${response.body()}")
                tickers = if (response.body() != null) Ticker.BasicTicker(
                    response.body()!!.price,
                    "tBTCUSD",
                    exchangeName()
                ) else null
            } else {
                return Failure(Exception("${exchangeName()} call failed ${response.code()}"))
            }

        } catch (e: Exception) {
            return Failure(e)
        }

        if (tickers == null) {
            return Failure(Exception("${exchangeName()} no tickers received"))
        }

        return Success(tickers)
    }
}