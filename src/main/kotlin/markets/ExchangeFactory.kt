package markets

import markets.crypto_exchanges.Exchange
import markets.crypto_exchanges.binance.BinanceExchange
import markets.crypto_exchanges.luno.LunoExchange

/**
 * Created by johannesC on 2017/09/09.
 */
class ExchangeFactory {

    fun getExchange(name : String) : Exchange = when(name) {
        LunoExchange.exchangeName -> LunoExchange()
        BinanceExchange.exchangeName -> BinanceExchange()
        else -> throw Exception("Unsupported exchange")
    }
}