package me.kyuubiran.bangumi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val adapter = BangumiListAdapter()
    private lateinit var lm: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FramgentBangumiListBinding.inflate(inflater, container, false)
        lm = LinearLayoutManager(context)
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

                if (bangumiList.isEmpty()) {
                    adapter.bangumiList.add(Bangumi("狐妖小红娘", "来相思树下", 48, 148).apply { id = 1L })
                    adapter.bangumiList.add(Bangumi("我推的狐狸", "喵喵", 1, 12).apply { id = 2L })
                    Log.d("BangumiListFragment", "Bangumi was empty")
                }
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
        binding.mainBgmLayout.adapter = adapter
        binding.mainBgmLayout.layoutManager = lm

        binding.mainBgmLayoutSwipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

        refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}