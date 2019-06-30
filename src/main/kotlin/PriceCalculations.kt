/**
 * @Author: johannesC
 * @Date: 2019-06-01, Sat
 **/
object PriceCalculations {

    fun getPriceChangePercentage(lastValue: Double, newValue: Double): Double =
        if (lastValue == 0.000) lastValue
        else ((newValue - lastValue) / lastValue) * 100.0

}