package helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Author: johannesC
 * @Date: 2019-07-02, Tue
 **/
fun execute(block: suspend () -> Unit) = GlobalScope.launch(Dispatchers.Default) { block.invoke() }

fun io(block: suspend () -> Unit) = GlobalScope.launch(Dispatchers.IO) { block.invoke() }

suspend fun <T> ior(block: suspend () -> T) = withContext(Dispatchers.IO) { block.invoke() }