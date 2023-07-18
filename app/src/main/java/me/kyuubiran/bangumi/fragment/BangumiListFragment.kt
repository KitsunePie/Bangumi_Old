package me.kyuubiran.bangumi.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.adapter.BangumiListAdapter
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.data.Bangumi
import me.kyuubiran.bangumi.databinding.FramgentBangumiListBinding
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.coWithMain
import me.kyuubiran.bangumi.utils.runSuspend

class BangumiListFragment : Fragment() {
    private var _binding: FramgentBangumiListBinding? = null
    private val binding get() = _binding!!

    private val adapter = BangumiListAdapter().apply {
        fragment = this@BangumiListFragment
    }
    private lateinit var lm: LinearLayoutManager

    val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FramgentBangumiListBinding.inflate(inflater, container, false)
        lm = LinearLayoutManager(context)

        binding.bangmuListAddFab.setOnClickListener {
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

            runSuspend {
                val db = AppDatabase.db.bangumiDao()
                val bangumiList: MutableList<Bangumi> = db.getAllBangumis().toMutableList()
                adapter.bangumiList = bangumiList
//                For test
//                if (bangumiList.isEmpty()) {
//                    adapter.bangumiList.add(Bangumi("狐妖小红娘", "来相思树下", 48, 148).apply { id = 1L })
//                    adapter.bangumiList.add(Bangumi("我推的狐狸", "喵喵", 1, 12).apply { id = 2L })
//                    Log.d("BangumiListFragment", "Bangumi was empty")
//                }
            }

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