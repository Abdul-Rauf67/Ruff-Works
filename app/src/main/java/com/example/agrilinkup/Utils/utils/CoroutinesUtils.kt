package com.example.agrilinkup.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <T> withMainContext(crossinline block: suspend () -> T): T {
    return withContext(Dispatchers.Main) {
        block.invoke()
    }
}

suspend inline fun <T> withIoContext(crossinline block: suspend () -> T): T {
    return withContext(Dispatchers.IO) {
        block.invoke()
    }
}

