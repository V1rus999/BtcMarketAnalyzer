package markets.crypto_exchanges.binance

import markets.Ticker
import markets.crypto_exchanges.CryptoExchange
import network.Failure
import network.Result
import network.RetrofitFinMarketApi
import network.Success
import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class BinanceExchange : CryptoExchange {

    companion object {
        const val exchangeName = "Binance"
    }

    private val ASK = 5
    private val requestUrl = HttpUrl.parse("https://api.binance.com/")
    private val retrofit =
        Retrofit.Builder().baseUrl(requestUrl!!).addConverterFactory(GsonConverterFactory.create()).build()
    private val btcApi = retrofit.create(RetrofitFinMarketApi::class.java)

    override fun exchangeName(): String = exchangeName

    override fun getTicker(): Result<Ticker.CryptoTicker, Exception> {
        val call = btcApi.getBinanceTicker()
        val tickers: Ticker.CryptoTicker?

        try {
            val response = call.execute()
            if (response.isSuccessful) {
                tickers = if (response.body() != null) Ticker.CryptoTicker(
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