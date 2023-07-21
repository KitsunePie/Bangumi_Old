package me.kyuubiran.bangumi.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.adapter.BangumiListAdapter
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.data.BangumiTag
import me.kyuubiran.bangumi.databinding.FramgentBangumiListBinding
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.coWithMain

class BangumiListFragment : Fragment() {
    private var _binding: FramgentBangumiListBinding? = null
    private val binding get() = _binding!!

    private val adapter = BangumiListAdapter()
    private lateinit var lm: LinearLayoutManager

    val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FramgentBangumiListBinding.inflate(inflater, container, false)
        lm = LinearLayoutManager(context)

        requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = getString(R.string.app_name)
        }

        binding.bangumiListAddFab.setOnClickListener {
            coLaunchIO {
                val item = adapter.newItem()
                BangumiModifyFragment.bangumiInEdit = item

                coWithMain { navController.navigate(R.id.action_BangumiListFragment_to_bangumiModifyFragment) }
            }
        }

        return binding.root
    }

    private fun refresh() {
        coLaunchIO {
            coWithMain {
                binding.mainBgmLayoutSwipeRefreshLayout.isRefreshing = true
            }

            val db = AppDatabase.db.bangumiDao()
            val bangumiList: MutableList<Bangumi> = db.getAllBangumis().toMutableList()
            adapter.bangumiList = bangumiList

            BangumiTag.allTagMap = AppDatabase.db.tagDao().getAllTags().associateBy { it.id }.toMutableMap()

            coWithMain {
                adapter.notifyDataSetChanged()
                binding.mainBgmLayoutSwipeRefreshLayout.isRefreshing = false
                Log.d("BangumiListFragment", "Data refreshed: size=${adapter.itemCount}")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> true
                    R.id.action_manage_tags -> {
                        navController.navigate(R.id.action_BangumiListFragment_to_tagListFragment)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.mainBgmLayout.adapter = adapter
        binding.mainBgmLayout.layoutManager = lm

        binding.mainBgmLayoutSwipeRefreshLayout.setOnRefreshListener { refresh() }

        refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}