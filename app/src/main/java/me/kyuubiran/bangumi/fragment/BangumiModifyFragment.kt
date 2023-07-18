package me.kyuubiran.bangumi.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.serialization.json.Json
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.databinding.FragmentModifiyBangumiBinding
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.coWithMain
import me.kyuubiran.bangumi.utils.runSuspend

class BangumiModifyFragment : Fragment() {
    private var _binding: FragmentModifiyBangumiBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        var bangumiInEdit: Bangumi? = null
    }

    private val navController by lazy { findNavController() }

    private fun applyChanges() {
        coLaunchIO {
            runSuspend {
                bangumiInEdit?.let {
                    it.title = binding.modifyBangumiTitleEt.text.toString()
                    it.desc = binding.modifyBangumiDescEt.text.toString()
                    it.currentEpisode = binding.modifyBangumiEpcurEt.text.toString().toIntOrNull() ?: it.currentEpisode
                    it.totalEpisode = binding.modifyBangumiEptotalEt.text.toString().toIntOrNull() ?: it.totalEpisode
                    it.desc = binding.modifyBangumiDescEt.text.toString()

                    val db = AppDatabase.db.bangumiDao()
                    db.update(it)
                    Log.d("BangumiModifyFragment", "Updated: ${Json.encodeToString(Bangumi.serializer(), it)}")
                }
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

        binding.bangmuModifySaveFab.setOnClickListener { applyChanges() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bangumiInEdit?.let {
            binding.modifyBangumiTitleEt.setText(it.title)
            binding.modifyBangumiDescEt.setText(it.desc)
            binding.modifyBangumiEpcurEt.setText(it.currentEpisode.toString())
            binding.modifyBangumiEptotalEt.setText(it.totalEpisode.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bangumiInEdit = null
    }
}