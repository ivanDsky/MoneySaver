package ua.zloyhr.moneysaver.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
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
            fabAddItem.setOnClickListener {

                findNavController(this@HomeFragment).navigate(R.id.action_miHome_to_addEditItemFragment)
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