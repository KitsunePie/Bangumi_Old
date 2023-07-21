package me.kyuubiran.bangumi.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.forEach
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.serialization.json.Json
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.databinding.FragmentModifiyBangumiBinding
import me.kyuubiran.bangumi.utils.DialogUtils
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.coWithMain
import me.kyuubiran.bangumi.utils.toDpInt
import me.kyuubiran.bangumi.view.TagSelectableItem

class BangumiModifyFragment : Fragment() {
    private var _binding: FragmentModifiyBangumiBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        var bangumiInEdit: Bangumi? = null
    }

    private val navController by lazy { findNavController() }

    private val tagList: MutableList<Int> = mutableListOf()

    private fun applyChanges() {
        coLaunchIO {
            bangumiInEdit?.let {
                it.title = binding.titleClickable.subtitleText.toString()
                it.desc = binding.descClickable.subtitleText.toString()
                it.currentEpisode = binding.curepClickable.subtitleText.toString().toIntOrNull() ?: it.currentEpisode
                it.totalEpisode = binding.ttlepClickable.subtitleText.toString().toIntOrNull() ?: it.totalEpisode
                it.tags = tagList

                val db = AppDatabase.db.bangumiDao()
                db.update(it)
                Log.d("BangumiModifyFragment", "Updated: ${Json.encodeToString(Bangumi.serializer(), it)}")
            }
            coWithMain {
                bangumiInEdit = null
                navController.navigateUp()
            }
        }
    }

    private fun onNavUp() {
        requireActivity().runOnUiThread {
            AlertDialog.Builder(requireContext()).setTitle(R.string.discard_changes)
                .setMessage(R.string.discard_changes_message)
                .setPositiveButton(R.string.discard) { _, _ ->
                    bangumiInEdit = null
                    navController.navigateUp()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentModifiyBangumiBinding.inflate(inflater, container, false)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onNavUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.bangumiModifySaveFab.setOnClickListener { applyChanges() }

        binding.titleClickable.setOnClickListener {
            DialogUtils.showEditTextDialog(requireContext(), getString(R.string.set_title), bangumiInEdit?.title) {
                binding.titleClickable.subtitleText = it
            }
        }

        binding.descClickable.setOnClickListener {
            DialogUtils.showEditTextDialog(requireContext(), getString(R.string.set_desc), bangumiInEdit?.desc) {
                binding.descClickable.subtitleText = it
            }
        }

        binding.curepClickable.setOnClickListener {
            DialogUtils.showEditTextDialog(
                requireContext(), getString(R.string.set_curep), bangumiInEdit?.currentEpisode?.toString(),
                inputType = InputType.TYPE_CLASS_NUMBER
            ) {
                binding.curepClickable.subtitleText = it.ifBlank { bangumiInEdit?.currentEpisode?.toString() ?: "1" }
            }
        }

        binding.ttlepClickable.setOnClickListener {
            DialogUtils.showEditTextDialog(
                requireContext(), getString(R.string.set_ttlep), bangumiInEdit?.totalEpisode?.toString(),
                inputType = InputType.TYPE_CLASS_NUMBER
            ) {
                binding.ttlepClickable.subtitleText = it.ifBlank { bangumiInEdit?.totalEpisode?.toString() ?: "0" }
            }
        }

        binding.tagsClickable.setOnClickListener {

            DialogUtils.showAlertDialog(requireContext()) {
                setTitle(R.string.set_tags)

                val scr = ScrollView(requireContext())
                scr.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val ll = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                    updatePadding(left = 8.toDpInt(requireContext()), right = 8.toDpInt(requireContext()))
                }

                scr.addView(ll)

                coLaunchIO {
                    coWithMain {
                        BangumiTag.allTagList.forEach {
                            ll.addView(TagSelectableItem(requireContext()).apply {
                                bangumiTag = it
                                isChecked = it.id in tagList
                            })
                        }
                    }
                }

                setNegativeButton(R.string.cancel, null)

                setPositiveButton(R.string.confirm) { _, _ ->
                    tagList.clear()
                    ll.forEach {
                        val tagSelectable = it as TagSelectableItem
                        if (tagSelectable.isChecked)
                            tagList.add(tagSelectable.bangumiTag.id)
                    }
                }

                setView(scr)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bangumiInEdit?.let {
            binding.titleClickable.subtitleText = it.title
            binding.descClickable.subtitleText = it.desc
            binding.curepClickable.subtitleText = it.currentEpisode.toString()
            binding.ttlepClickable.subtitleText = it.totalEpisode.toString()
            tagList.addAll(it.tags)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bangumiInEdit = null
    }
}