package markets.crypto_exchanges.bitfinex

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

class BitfinexExchange : CryptoExchange {

    private val ASK = 4
    private val requestUrl = HttpUrl.parse("https://api-pub.bitfinex.com/")
    private val retrofit =
        Retrofit.Builder().baseUrl(requestUrl!!).addConverterFactory(GsonConverterFactory.create()).build()
    private val btcApi = retrofit.create(RetrofitFinMarketApi::class.java)

    override fun exchangeName(): String = "Bitfinex"

    override fun getTicker(): Result<Ticker.CryptoTicker, Exception> {
        val call = btcApi.getBitfinexTicker()
        val tickers: Ticker.CryptoTicker?

        try {
            val response = call.execute()
            if (response.isSuccessful) {
                tickers = extractTickers(response.body())
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

    //Values on https://docs.bitfinex.com/v2/reference#rest-public-tickers
    private fun extractTickers(result: Array<Double>?): Ticker.CryptoTicker? {
        result?.let {
            return Ticker.CryptoTicker(result[ASK], "tBTCUSD", exchangeName())
        }
        return null
    }
}