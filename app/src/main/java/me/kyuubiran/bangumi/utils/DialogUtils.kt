package me.kyuubiran.bangumi.utils

import android.app.AlertDialog
import android.content.Context

object DialogUtils {
    inline fun showAlertDialog(ctx: Context, func: AlertDialog.Builder.() -> Unit): AlertDialog = AlertDialog.Builder(ctx).apply(func).show()
}