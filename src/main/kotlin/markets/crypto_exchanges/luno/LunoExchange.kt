package markets.crypto_exchanges.luno

import markets.Ticker
import markets.crypto_exchanges.Exchange
import markets.crypto_exchanges.binance.BinanceExchange
import helpers.Failure
import helpers.Result
import okhttp3.HttpUrl
import network.RetrofitFinMarketApi
import helpers.Success
import org.pmw.tinylog.Logger
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by johannesC on 2017/09/03.
 */
class LunoExchange : Exchange {

    companion object {
        const val exchangeName = "LunoSA"
        const val exchangePair = "btczar"
    }

    private val requestUrl = HttpUrl.parse("https://api.mybitx.com/")
    private val retrofit =
        Retrofit.Builder().baseUrl(requestUrl!!).addConverterFactory(GsonConverterFactory.create()).build()
    private val btcApi = retrofit.create(RetrofitFinMarketApi::class.java)

    override fun exchangeName(): String = exchangeName

    override suspend fun getTicker(): Result<Ticker.BasicTicker, Exception> {
        return try {
            val exchangeTicker = btcApi.getLunoTicker()
            Logger.debug("Got data from ${BinanceExchange.exchangeName} $exchangeTicker")
            val tickers = Ticker.BasicTicker(
                exchangeTicker.lastTrade ?: exchangeTicker.ask,
                exchangePair,
                exchangeName()
            )

            Success(tickers)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}