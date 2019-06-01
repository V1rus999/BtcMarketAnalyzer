package tickerHandling

import markets.Ticker

/**
 * @Author: JohannesC
 * @Date: 2019-06-01, Sat
 **/
interface TickerManager {

    fun newTickerResultReceived(
        timeStamp: Long,
        firstTicker: Ticker.CryptoTicker?,
        secondTicker: Ticker.CryptoTicker?
    )

}