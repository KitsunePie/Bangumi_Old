package me.kyuubiran.bangumi.utils

import android.content.Context
import android.view.View

fun Int.toDpFloat(context: Context) = convertToDpDimension(context)

fun Int.toDpInt(context: Context) = convertToDpDimension(context).toInt()

fun Float.toDpFloat(context: Context) = convertToDpDimension(context)

fun Float.toDpInt(context: Context) = convertToDpDimension(context).toInt()

fun Int.toDpFloat(view: View) = convertToDpDimension(view.context)

fun Int.toDpInt(view: View) = convertToDpDimension(view.context).toInt()

fun Float.toDpFloat(view: View) = convertToDpDimension(view.context)

fun Float.toDpInt(view: View) = convertToDpDimension(view.context).toInt()

fun Int.toPxFloat(context: Context) = convertToPxDimension(context)

fun Int.toPxInt(context: Context) = convertToPxDimension(context).toInt()

fun Float.toPxFloat(context: Context) = convertToPxDimension(context)

fun Float.toPxInt(context: Context) = convertToPxDimension(context).toInt()

fun Int.toPxFloat(view: View) = convertToPxDimension(view.context)

fun Int.toPxInt(view: View) = convertToPxDimension(view.context).toInt()

fun Float.toPxFloat(view: View) = convertToPxDimension(view.context)

fun Float.toPxInt(view: View) = convertToPxDimension(view.context).toInt()

private fun Number.convertToDpDimension(context: Context) =
    if (toFloat() >= 0f) toFloat() * context.resources.displayMetrics.density else toFloat()

private fun Number.convertToPxDimension(context: Context) =
    if (toFloat() >= 0f) toFloat() / context.resources.displayMetrics.density + 0.5f else toFloat()