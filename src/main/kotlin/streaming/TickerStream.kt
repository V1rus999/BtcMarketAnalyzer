package streaming

/**
 * @Author: johannesC
 * @Date: 2019-06-30, Sun
 **/
interface TickerStream {

    fun startDownloadingTickerData()

    fun stopDownloadingTickerData()
}