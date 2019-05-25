package markets

import markets.crypto_exchanges.CryptoExchange
import markets.crypto_exchanges.bitfinex.BitfinexExchange
import markets.crypto_exchanges.bittrex.BittrexExchange
import markets.crypto_exchanges.luno.LunoExchange

/**
 * Created by johannesC on 2017/09/09.
 */
class ExchangeFactory {

    fun getExchanges(): List<CryptoExchange> {
        return arrayListOf(
            LunoExchange(),
            BitfinexExchange()
        )
    }
}