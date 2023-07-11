package me.kyuubiran.bangumi.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend inline fun <R> runSuspend(crossinline func: suspend () -> R) = suspend { func() }()
suspend inline fun <R> coWithMain(crossinline func: suspend () -> R) = withContext(Dispatchers.Main) { func() }
suspend inline fun <R> coWithIO(crossinline func: suspend () -> R) = withContext(Dispatchers.IO) { func() }
suspend inline fun <R> coWithDefault(crossinline func: suspend () -> R) = withContext(Dispatchers.Default) { func() }
inline fun <R> coLaunchMain(crossinline func: suspend () -> R) = CoroutineScope(Dispatchers.Main).launch { func() }
inline fun <R> coLaunchIO(crossinline func: suspend () -> R) = CoroutineScope(Dispatchers.IO).launch { func() }
inline fun <R> coLaunchDefault(crossinline func: suspend () -> R) = CoroutineScope(Dispatchers.Default).launch { func() }