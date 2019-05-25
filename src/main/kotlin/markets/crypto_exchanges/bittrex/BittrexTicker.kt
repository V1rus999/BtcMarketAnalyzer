package markets.crypto_exchanges.bittrex

/**
 * Created by johannesC on 2017/09/09.
 */
class BittrexTicker {

    data class BittrexResult(
            var success: Boolean? = null,
            var message: String? = null,
            var result: List<BittrexTickerDetails>? = null
    )

    data class BittrexTickerDetails(
        var MarketName: String? = null,
        var High: Float? = null,
        var Low: Float? = null,
        var Volume: Float? = null,
        val Last: Double,
        var BaseVolume: Float? = null,
        var TimeStamp: String? = null,
        var Bid: Float? = null,
        var Ask: Float? = null,
        var OpenBuyOrders: Int? = null,
        var OpenSellOrders: Int? = null,
        var PrevDay: Float? = null,
        var Created: String? = null

    )
}