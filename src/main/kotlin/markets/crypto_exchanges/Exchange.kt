package markets.crypto_exchanges

import markets.Ticker
import helpers.Result
import java.lang.Exception

/**
 * Created by johannesC on 2017/09/03.
 */
interface Exchange {

    fun exchangeName() : String

    fun getTicker(): Result<Ticker.BasicTicker, Exception>
}