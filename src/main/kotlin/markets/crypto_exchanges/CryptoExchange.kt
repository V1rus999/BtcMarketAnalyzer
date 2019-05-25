package markets.crypto_exchanges

import markets.Ticker
import network.Result
import java.lang.Exception

/**
 * Created by johannesC on 2017/09/03.
 */
interface CryptoExchange {

    fun exchangeName() : String

    fun getTicker(): Result<Ticker.CryptoTicker, Exception>

}