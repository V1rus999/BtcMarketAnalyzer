package markets

import markets.crypto_exchanges.CryptoExchange
import markets.crypto_exchanges.binance.BinanceExchange
import markets.crypto_exchanges.luno.LunoExchange

/**
 * Created by johannesC on 2017/09/09.
 */
class ExchangeFactory {

    fun getExchange(name : String) : CryptoExchange = when(name) {
        LunoExchange.exchangeName -> LunoExchange()
        BinanceExchange.exchangeName -> BinanceExchange()
        else -> throw Exception("Unsupported exchange")
    }

    fun getExchanges(): List<CryptoExchange> {
        return arrayListOf(
            LunoExchange(),
            BinanceExchange()
        )
    }
}