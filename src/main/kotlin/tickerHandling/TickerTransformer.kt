package tickerHandling

import markets.Ticker
import org.decimal4j.util.DoubleRounder

/**
 * @Author: johannesC
 * @Date: 2019-06-30, Sun
 **/
object TickerTransformer {

    fun transformTickers(
        timeStamp: String,
        newTickers: List<Ticker.BasicTicker>,
        previousTicker: Ticker.OutputTicker?
    ): Ticker.OutputTicker {
        val priceWrappedTickers = newTickers.map { thisTicker ->
            Ticker.PriceMovementWrappedTicker(
                getPriceIncreasePercentage(findTickerPriceForExchange(previousTicker, thisTicker.exchange), thisTicker),
                thisTicker
            )
        }
        return Ticker.OutputTicker(timeStamp, priceWrappedTickers)
    }

    private fun findTickerPriceForExchange(oldTicker: Ticker.OutputTicker?, exchange: String): Ticker.BasicTicker? {
        val result = oldTicker?.tickers?.find {
            it.ticker.exchange == exchange
        }
        return result?.ticker
    }


    private fun getPriceIncreasePercentage(lastValue: Ticker.BasicTicker?, ticker: Ticker.BasicTicker): Double {
        val result = PriceCalculations.getPriceChangePercentage(lastValue?.price ?: 0.000, ticker.price)
        return DoubleRounder.round(result, 3)
    }
}