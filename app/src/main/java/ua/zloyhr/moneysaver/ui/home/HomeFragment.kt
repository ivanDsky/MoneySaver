package ua.zloyhr.moneysaver.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import ua.zloyhr.moneysaver.R
import ua.zloyhr.moneysaver.data.db.SortQueryBy
import ua.zloyhr.moneysaver.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding : FragmentHomeBinding
    private lateinit var adapter: ChargeListAdapter

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        adapter = ChargeListAdapter(findNavController(this))
        binding.rvChargeList.adapter = adapter
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.queryFlow.collect {
                adapter.submitList(it)
            }
        }
        binding.apply {
            rvChargeList.layoutManager = LinearLayoutManager(this@HomeFragment.context)
            fabAddItem.setOnClickListener {

                findNavController(this@HomeFragment).navigate(R.id.action_miHome_to_addEditItemFragment)
            }
        }

        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu, menu)

        val searchItem = menu.findItem(R.id.miSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onQueryChanged(newText?:"")
                return true
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miSortByName -> viewModel.onSortedMenuClick(SortQueryBy.NAME)
            R.id.miSortByValue -> viewModel.onSortedMenuClick(SortQueryBy.VALUE)
            R.id.miSortByDate -> viewModel.onSortedMenuClick(SortQueryBy.TIME_CREATED,true)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}