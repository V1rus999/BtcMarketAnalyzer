package markets

import java.util.*

/**
 * Created by johannesC on 2017/09/03.
 */
class Ticker {

    data class BasicTicker(
        val price: Double,
        val pair: String,
        val exchange: String
    )

    data class OutputTicker(val timestamp: String, val tickers: List<PriceMovementWrappedTicker>)
    data class PriceMovementWrappedTicker(
        val priceMovement: Double,
        val ticker: BasicTicker
    )

    sealed class TickerTypes {
        data class LunoTicker(val priceMovement: Double, val ticker: BasicTicker) : TickerTypes()
        data class BinanceTicker(val priceMovement: Double, val ticker: BasicTicker) : TickerTypes()
    }

    data class TrackedTicker(
        val timestamp: String,
        val cryptoPair: Pair<TickerTypes.LunoTicker, TickerTypes.BinanceTicker>
    )

    data class OutputCryptoTicker(
        val timeStamp: String? = Calendar.getInstance().time.toString(),
        val basicTickers: ArrayList<BasicTicker> = arrayListOf(),
        val zar: String = "",
        val eur: String = ""
    )

    data class FiatTicker(val base: String? = null, val date: String? = null, val rates: Map<String, Double>? = null)

    class Rates(val name: String = "", val value: Double = 0.0)
}