package markets.crypto_exchanges.luno

import markets.Ticker
import markets.crypto_exchanges.Exchange
import markets.crypto_exchanges.binance.BinanceExchange
import network.Failure
import network.Result
import okhttp3.HttpUrl
import network.RetrofitFinMarketApi
import network.Success
import org.pmw.tinylog.Logger
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by johannesC on 2017/09/03.
 */
class LunoExchange : Exchange {

    companion object {
        const val exchangeName = "LunoSA"
    }

    private val requestUrl = HttpUrl.parse("https://api.mybitx.com/")
    private val retrofit =
        Retrofit.Builder().baseUrl(requestUrl!!).addConverterFactory(GsonConverterFactory.create()).build()
    private val btcApi = retrofit.create(RetrofitFinMarketApi::class.java)

    override fun exchangeName(): String = exchangeName

    override fun getTicker(): Result<Ticker.BasicTicker, Exception> {
        val call = btcApi.getLunoTicker()
        val tickers: Ticker.BasicTicker?

        try {
            val response = call.execute()
            if (response.isSuccessful) {
                Logger.debug("Got data from ${BinanceExchange.exchangeName} ${response.body()}")
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

    private fun extractTickers(result: LunoTicker?): Ticker.BasicTicker? =
        result?.let {
            Ticker.BasicTicker(
                result.lastTrade ?: result.ask,
                "btczar",
                exchangeName()
            )
        }
}