package markets.crypto_exchanges.bittrex

import markets.Ticker
import markets.crypto_exchanges.Exchange
import helpers.Failure
import helpers.Result
import okhttp3.HttpUrl
import network.RetrofitFinMarketApi
import helpers.Success
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by johannesC on 2017/09/09.
 */
class BittrexExchange : Exchange {

    private val requestUrl = HttpUrl.parse("https://bittrex.com/")
    private val retrofit =
        Retrofit.Builder().baseUrl(requestUrl!!).addConverterFactory(GsonConverterFactory.create()).build()
    private val btcApi = retrofit.create(RetrofitFinMarketApi::class.java)

    override fun exchangeName(): String = "Bittrex"

    override fun getTicker(): Result<Ticker.BasicTicker, java.lang.Exception> {
        val call = btcApi.getBittrexTicker()
        val tickers: Ticker.BasicTicker?

        try {
            val response = call.execute()
            if (response.isSuccessful) {
                tickers = extractTickers(response.body())
            }else {
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

    private fun extractTickers(result: BittrexTicker.BittrexResult?): Ticker.BasicTicker? {
        val basicTickers: List<Ticker.BasicTicker>? = result?.result?.mapNotNull {
            val pair = it.MarketName?.toLowerCase()?.replace("-", "")
            if (pair != null) {
                Ticker.BasicTicker(it.Last, pair, exchangeName())
            } else {
                null
            }
        }
        //todo fix this
        return null
    }
}