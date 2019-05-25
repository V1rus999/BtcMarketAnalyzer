package markets.fiat_exchanges

import markets.Ticker

/**
 * Created by johannesC on 2017/09/05.
 */
interface FiatExchange {

    fun getRates() : ArrayList<Ticker.Rates>
}