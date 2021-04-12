package ua.zloyhr.moneysaver.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import ua.zloyhr.moneysaver.R
import ua.zloyhr.moneysaver.data.db.ShowQuery
import ua.zloyhr.moneysaver.data.db.SortQueryBy
import ua.zloyhr.moneysaver.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ChargeListAdapter
    private lateinit var searchView: SearchView
    private lateinit var sharedPreferences: SharedPreferences

    private val rotateOpen :Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_open_anim) }
    private val rotateClose :Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_close_anim) }
    private val fromBottom :Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.from_bottom_anim) }
    private val toBottom :Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.to_bottom_anim) }

    private var isFabExpanded = false

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = view.context.getSharedPreferences("homePref", Context.MODE_PRIVATE)
        viewModel.onLoadPreferences(sharedPreferences)

        binding = FragmentHomeBinding.bind(view)
        adapter = ChargeListAdapter(findNavController(this))
        binding.rvChargeList.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.queryFlow.collect {
                adapter.submitList(it)
                viewModel.onSavePreferences(sharedPreferences)
            }
        }
        binding.apply {
            rvChargeList.layoutManager = LinearLayoutManager(this@HomeFragment.context)
            fabExpandAddItem.setOnClickListener {
                onFabExpandedClick();
            }

            fabPosItem.setOnClickListener {
                val action = HomeFragmentDirections.actionMiHomeToAddEditItemFragment(null,"Add positive item")
                findNavController().navigate(action)
            }

            fabNegItem.setOnClickListener {
                val action = HomeFragmentDirections.actionMiHomeToAddEditItemFragment(null,"Add negative item")
                findNavController().navigate(action)
            }
        }

        val helper = ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.currentList[viewHolder.adapterPosition]
                viewModel.onDeleteItem(item)
                val snackbar = Snackbar.make(viewHolder.itemView.rootView,"Item deleted",Snackbar.LENGTH_LONG)
                    .setAction("UNDO"){
                        viewModel.onInsertItem(item)
                    }
                val btn = snackbar.view.findViewById(com.google.android.material.R.id.snackbar_action) as Button
                btn.setBackgroundColor(Color.TRANSPARENT)
                snackbar.show()
            }

        })
        helper.attachToRecyclerView(binding.rvChargeList)

        setHasOptionsMenu(true)
    }

    private fun onFabExpandedClick() {
        isFabExpanded = binding.fabPosItem.visibility == View.VISIBLE
        if(!isFabExpanded){
            binding.apply {
                fabPosItem.startAnimation(fromBottom)
                fabNegItem.startAnimation(fromBottom)
                fabExpandAddItem.startAnimation(rotateOpen)

                fabPosItem.visibility = View.VISIBLE
                fabNegItem.visibility = View.VISIBLE

                fabPosItem.isClickable = true
                fabNegItem.isClickable = true
            }
        }else{
            binding.apply {
                fabPosItem.startAnimation(toBottom)
                fabNegItem.startAnimation(toBottom)
                fabExpandAddItem.startAnimation(rotateClose)

                fabPosItem.visibility = View.INVISIBLE
                fabNegItem.visibility = View.INVISIBLE

                fabPosItem.isClickable = false
                fabNegItem.isClickable = false
            }
        }
        isFabExpanded = !isFabExpanded
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_home_menu, menu)

        val searchItem = menu.findItem(R.id.miSearch)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.queryStringFlow.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onQueryChanged(newText ?: "")
                return true
            }
        })

        val positiveItem = menu.findItem(R.id.miShowPositive)
        val negativeItem = menu.findItem(R.id.miShowNegative)
        val resetItem = menu.findItem(R.id.miResetFilters)

        positiveItem.setOnMenuItemClickListener {
            viewModel.onShowFilterClick(ShowQuery.POSITIVE)
            true
        }

        negativeItem.setOnMenuItemClickListener {
            viewModel.onShowFilterClick(ShowQuery.NEGATIVE)
            true
        }

        resetItem.setOnMenuItemClickListener {
            viewModel.onResetFilters()
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchItem.collapseActionView()
            true
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSavedInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miSortByName -> viewModel.onSortedMenuClick(SortQueryBy.NAME)
            R.id.miSortByValue -> viewModel.onSortedMenuClick(SortQueryBy.VALUE)
            R.id.miSortByDate -> viewModel.onSortedMenuClick(SortQueryBy.TIME_CREATED, true)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}