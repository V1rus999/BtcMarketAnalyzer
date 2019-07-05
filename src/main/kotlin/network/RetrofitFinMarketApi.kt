package network

import markets.Ticker
import markets.crypto_exchanges.binance.BinanceTicker
import markets.crypto_exchanges.bittrex.BittrexTicker
import retrofit2.http.GET
import markets.crypto_exchanges.luno.LunoTicker
import retrofit2.Call

/**
 * Created by johannesC on 2017/09/03.
 */
interface RetrofitFinMarketApi {

    @GET("api/1/ticker?pair=XBTZAR")
    suspend fun getLunoTicker(): LunoTicker

    @GET("latest?base=USD")
    fun getFiatTicker(): Call<Ticker.FiatTicker>

    @GET("api/v1.1/public/getmarketsummaries")
    fun getBittrexTicker(): Call<BittrexTicker.BittrexResult>

    @GET("api/v3/ticker/price?symbol=BTCUSDT")
    suspend fun getBinanceTicker(): BinanceTicker

}