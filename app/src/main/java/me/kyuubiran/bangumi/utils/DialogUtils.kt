package me.kyuubiran.bangumi.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import me.kyuubiran.bangumi.R

object DialogUtils {
    inline fun showAlertDialog(ctx: Context, func: AlertDialog.Builder.() -> Unit): AlertDialog = AlertDialog.Builder(ctx).apply(func).show()

    fun showEditTextDialog(
        ctx: Context,
        title: String,
        defText: String? = null,
        hint: String? = null,
        textFilter: Array<InputFilter>? = null,
        inputType: Int? = null,
        onConfirm: (String) -> Unit
    ): AlertDialog {
//        val ll = LinearLayout(ctx).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(32.toDpInt(ctx), 0, 32.toDpInt(ctx), 0)
//            }
//            orientation = LinearLayout.HORIZONTAL
//        }

        val editText = EditText(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32.toDpInt(ctx), 0, 32.toDpInt(ctx), 0)
            }

            setText(defText)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setHint(hint)
            setInputType(inputType ?: InputType.TYPE_CLASS_TEXT)
            textFilter?.let { filters = it }
        }

//        ll.addView(editText)

        return showAlertDialog(ctx) {
            setTitle(title)
            setView(editText)
            setPositiveButton(android.R.string.ok) { _, _ -> onConfirm(editText.text.toString()) }
            setNegativeButton(android.R.string.cancel, null)
        }
    }

    fun showConfirmDialog(
        ctx: Context,
        title: String,
        message: String,
        cancelable: Boolean = true,
        onConfirm: () -> Unit
    ): AlertDialog = showAlertDialog(ctx)
    {
        setTitle(title)
        setMessage(message)
        setCancelable(cancelable)
        setPositiveButton(R.string.confirm) { _, _ -> onConfirm() }
        setNegativeButton(R.string.cancel, null)
    }
}