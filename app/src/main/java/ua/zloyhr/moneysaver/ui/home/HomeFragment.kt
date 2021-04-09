package ua.zloyhr.moneysaver.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ua.zloyhr.moneysaver.R
import ua.zloyhr.moneysaver.databinding.FragmentHomeBinding
import ua.zloyhr.moneysaver.ui.additem.AddEditItemFragment

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding : FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getItems().collect {
                binding.rvChargeList.adapter = ChargeListAdapter(findNavController(this@HomeFragment),it)
            }
        }
        binding.apply {
            rvChargeList.layoutManager = LinearLayoutManager(this@HomeFragment.context)
            fabAddItem.setOnClickListener {

                findNavController(this@HomeFragment).navigate(R.id.action_miHome_to_addEditItemFragment)
            }
        }
    }
}