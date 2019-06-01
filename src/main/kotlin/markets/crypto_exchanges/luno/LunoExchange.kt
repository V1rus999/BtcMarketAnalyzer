package markets.crypto_exchanges.luno

import markets.Ticker
import markets.crypto_exchanges.CryptoExchange
import network.Failure
import network.Result
import okhttp3.HttpUrl
import network.RetrofitFinMarketApi
import network.Success
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by johannesC on 2017/09/03.
 */
class LunoExchange : CryptoExchange {

    companion object {
        const val exchangeName = "LunoSA"
    }

    private val requestUrl = HttpUrl.parse("https://api.mybitx.com/")
    private val retrofit =
        Retrofit.Builder().baseUrl(requestUrl!!).addConverterFactory(GsonConverterFactory.create()).build()
    private val btcApi = retrofit.create(RetrofitFinMarketApi::class.java)

    override fun exchangeName(): String = exchangeName

    override fun getTicker(): Result<Ticker.CryptoTicker, Exception> {
        val call = btcApi.getLunoTicker()
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

    private fun extractTickers(result: LunoTicker?): Ticker.CryptoTicker? =
        result?.let {
            Ticker.CryptoTicker(result.ask, "btczar", exchangeName())
        }
}