package me.kyuubiran.bangumi.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.forEach
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.serialization.json.Json
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.databinding.FragmentModifiyBangumiBinding
import me.kyuubiran.bangumi.utils.DialogUtils
import me.kyuubiran.bangumi.utils.Utils
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.coWithMain
import me.kyuubiran.bangumi.utils.toDpInt
import me.kyuubiran.bangumi.view.ImageTextButton
import me.kyuubiran.bangumi.view.TagSelectableItem

class BangumiModifyFragment : Fragment() {
    private var _binding: FragmentModifiyBangumiBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        var bangumiInEdit: Bangumi? = null
    }

    private val navController by lazy { findNavController() }

    private val tagList: MutableList<Long> = mutableListOf()

    private lateinit var coverImageView: ImageView

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
            } ?: run { Log.e("BangumiModifyFragment", "bangumiInEdit is null") }
            coWithMain {
                bangumiInEdit = null
                navController.navigateUp()
            }
        }
    }

    private fun onNavUp() {
        requireActivity().runOnUiThread {
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.discard_changes)
                .setMessage(R.string.discard_changes_message)
                .setPositiveButton(R.string.discard) { _, _ ->
                    bangumiInEdit = null
                    navController.navigateUp()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun showEditCoverDialog(ctx: Context) {
        val pic = bangumiInEdit?.let { Utils.getCoverImage(ctx, it) }
        val has = pic != null

        val removeCover = ImageTextButton(ctx).apply {
            text = getString(R.string.remove_cover)
            drawable = AppCompatResources.getDrawable(ctx, R.drawable.baseline_delete_forever_24)
        }

        val setCover = ImageTextButton(ctx).apply {
            text = getString(R.string.select_from_file)
            drawable = AppCompatResources.getDrawable(ctx, R.drawable.baseline_file_copy_24)
        }

        val dialog = DialogUtils.showAlertDialog(ctx) {
            setTitle(getString(R.string.set_cover))

            val ll = LinearLayout(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                updatePadding(16.toDpInt(context), 8.toDpInt(context), 16.toDpInt(context), 8.toDpInt(context))

                gravity = LinearLayout.HORIZONTAL
                orientation = LinearLayout.VERTICAL

                if (has)
                    addView(removeCover)

                addView(setCover)
            }

            setView(ll)
        }

        removeCover.setOnClickListener {
            DialogUtils.showConfirmDialog(ctx, getString(R.string.remove_cover), getString(R.string.remove_cover_message)) {
                val bgm = bangumiInEdit ?: return@showConfirmDialog
                Utils.deleteCoverImage(ctx, bgm)
                coverImageView.setImageDrawable(null)
                dialog.dismiss()
            }
        }

        setCover.setOnClickListener {
            dialog.dismiss()
            pickCover()
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        val bgm = bangumiInEdit ?: return@registerForActivityResult
        Log.d("BangumiModifyFragment", "uri: $uri, path: ${uri.path}")

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        inputStream?.use { input ->
            Utils.saveCoverImage(requireContext(), bgm, input)
        }
        coverImageView.setImageBitmap(Utils.getCoverImage(requireContext(), bgm))
    }

    private fun pickCover() {
        getContent.launch("image/*")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentModifiyBangumiBinding.inflate(inflater, container, false)

        requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = getString(R.string.edit_with, bangumiInEdit?.title ?: "<Unknown>")
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onNavUp()
            }
        }

        coverImageView = ImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                80.toDpInt(requireContext()),
                120.toDpInt(requireContext())
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.bangumiModifySaveFab.setOnClickListener { applyChanges() }

        binding.coverClickable.rightLayout.addView(coverImageView)
        coverImageView.setImageBitmap(bangumiInEdit?.let { Utils.getCoverImage(requireContext(), it) })

        binding.coverClickable.setOnClickListener {
            showEditCoverDialog(requireContext())
        }

        binding.titleClickable.setOnClickListener {
            DialogUtils.showEditTextDialog(requireContext(), getString(R.string.set_title), binding.titleClickable.subtitleText.toString()) {
                binding.titleClickable.subtitleText = it
            }
        }

        binding.descClickable.setOnClickListener {
            DialogUtils.showEditTextDialog(requireContext(), getString(R.string.set_desc), binding.descClickable.subtitleText.toString()) {
                binding.descClickable.subtitleText = it
            }
        }

        binding.curepClickable.setOnClickListener {
            DialogUtils.showEditTextDialog(
                requireContext(), getString(R.string.set_curep),
                binding.curepClickable.subtitleText.toString(),
                inputType = InputType.TYPE_CLASS_NUMBER
            ) {
                binding.curepClickable.subtitleText = it.ifBlank { bangumiInEdit?.currentEpisode?.toString() ?: "1" }
            }
        }

        binding.ttlepClickable.setOnClickListener {
            DialogUtils.showEditTextDialog(
                requireContext(), getString(R.string.set_ttlep),
                binding.ttlepClickable.subtitleText.toString(),
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