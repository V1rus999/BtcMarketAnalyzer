package markets.crypto_exchanges.luno

/**
 * Created by johannesC on 2017/09/03.
 */
data class LunoTicker(
        var timestamp: Long? = null,
        var bid: String? = null,
        val ask: Double,
        val lastTrade: Double? = null,
        var rolling24HourVolume: String? = null,
        var pair: String? = null)

