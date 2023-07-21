package me.kyuubiran.bangumi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import me.kyuubiran.bangumi.R
import me.kyuubiran.bangumi.adapter.FragmentTagListAdapter
import me.kyuubiran.bangumi.data.AppDatabase
import me.kyuubiran.bangumi.databinding.FragmentTagListBinding
import me.kyuubiran.bangumi.utils.coLaunchIO
import me.kyuubiran.bangumi.utils.coWithMain

class BangumiTagListFragment : Fragment() {
    private var _binding: FragmentTagListBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        val adapter = FragmentTagListAdapter()
        coLaunchIO {
            adapter.tagList = AppDatabase.db.tagDao().getAllTags().toMutableList()
        }

        adapter
    }
    private lateinit var lm: LinearLayoutManager

    private val navController by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTagListBinding.inflate(inflater, container, false)
        lm = LinearLayoutManager(context)

        binding.tagListAddFab.setOnClickListener {
            coLaunchIO {
                val item = adapter.newItem()
                BangumiTagModifyFragment.tagInEdit = item
                coWithMain { navController.navigate(R.id.action_tagListFragment_to_tagModifyFragment) }
            }
        }

        binding.tagList.adapter = adapter
        binding.tagList.layoutManager = lm

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}