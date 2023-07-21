package me.kyuubiran.bangumi.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.databinding.FragmentModifyTagBinding
import me.kyuubiran.bangumi.utils.DialogUtils
import me.kyuubiran.bangumi.utils.Utils.colorStr
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.toDpInt
import me.kyuubiran.bangumi.view.CircleView
import me.kyuubiran.bangumi.view.ColorPickerView

class BangumiTagModifyFragment : Fragment() {
    companion object {
        @JvmStatic
        var tagInEdit: BangumiTag? = null
    }

    private var _binding: FragmentModifyTagBinding? = null
    private val binding get() = _binding!!

    private val colorView by lazy {
        val view = CircleView(requireContext())

        view.layoutParams = ViewGroup.LayoutParams(
            20.toDpInt(requireContext()),
            20.toDpInt(requireContext())
        )

        view
    }

    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentModifyTagBinding.inflate(inflater, container, false)

        requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = getString(R.string.edit_with, tagInEdit?.name ?: "<Unknown>")
        }

        tagInEdit?.let {
            binding.modifyTagName.subtitleText = it.name
            binding.modifyTagColor.subtitleText = it.color.colorStr()
            binding.modifyTagPriority.subtitleText = it.priority.toString()
            colorView.setCircleColor(it.color)
        } ?: kotlin.run { Log.e("BangumiTagModifyFragment", "tagInEdit is null!") }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onNavUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.modifyTagSaveFab.setOnClickListener {
            tagInEdit?.let { tag ->
                tag.name = binding.modifyTagName.subtitleText.toString()
                tag.color = runCatching { Color.parseColor(binding.modifyTagColor.subtitleText.toString()) }.getOrElse { tag.color }
                tag.priority = binding.modifyTagPriority.subtitleText.toString().toIntOrNull() ?: tag.priority

                coLaunchIO {
                    AppDatabase.db.tagDao().update(tag)
                    Log.d("BangumiTagModifyFragment", "Updated ${Json.encodeToString(tag)}}")
                }
                nav.navigateUp()
            } ?: kotlin.run { Log.e("BangumiTagModifyFragment", "tagInEdit is null!") }
        }

        binding.modifyTagColor.rightLayout.addView(colorView)

        binding.modifyTagName.setOnClickListener {
            DialogUtils.showEditTextDialog(requireContext(), getString(R.string.set_name), binding.modifyTagName.subtitleText.toString()) {
                binding.modifyTagName.subtitleText = it
            }
        }

        binding.modifyTagColor.setOnClickListener {
            DialogUtils.showAlertDialog(requireContext()) {
                setTitle(getString(R.string.set_color))
                val picker = ColorPickerView(requireContext())

                runCatching {
                    picker.setColor(Color.parseColor(binding.modifyTagColor.subtitleText.toString()))
                }.onFailure {
                    Log.w("BangumiTagModifyFragment", "Failed to parse color: ${binding.modifyTagColor.subtitleText.toString()}")
                }

                setNegativeButton(R.string.cancel, null)
                setPositiveButton(R.string.confirm) { _, _ ->
                    binding.modifyTagColor.subtitleText = picker.getColor().colorStr()
                    colorView.setCircleColor(picker.getColor())
                }

                setView(picker)
            }

//            DialogUtils.showEditTextDialog(
//                requireContext(),
//                getString(R.string.set_color),
//                binding.modifyTagColor.subtitleText.toString()
//            ) { color ->
//                try {
//                    colorView.setBackgroundColor(Color.parseColor(color))
//                    binding.modifyTagColor.subtitleText = color
//                } catch (e: Exception) {
//                    Log.w("BangumiTagModifyFragment", "Failed to parse color: $color")
////                    Snackbar.make(container!!, getString(R.string.color_parse_error, color), Snackbar.LENGTH_SHORT).show()
//                }
//            }
        }

        binding.modifyTagPriority.setOnClickListener {
            DialogUtils.showEditTextDialog(
                requireContext(),
                getString(R.string.set_priority),
                binding.modifyTagPriority.subtitleText.toString(),
                inputType = InputType.TYPE_CLASS_NUMBER
            ) {
                binding.modifyTagPriority.subtitleText = it
            }
        }

        return binding.root
    }

    private fun onNavUp() {
        requireActivity().runOnUiThread {
            AlertDialog.Builder(requireContext()).setTitle(R.string.discard_changes)
                .setMessage(R.string.discard_changes_message)
                .setPositiveButton(R.string.discard) { _, _ ->
                    BangumiModifyFragment.bangumiInEdit = null
                    nav.navigateUp()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
}