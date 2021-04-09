package ua.zloyhr.moneysaver.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ua.zloyhr.moneysaver.R
import ua.zloyhr.moneysaver.data.entities.ChargeItem
import ua.zloyhr.moneysaver.databinding.ItemChargeBinding
import java.text.DateFormat
import java.util.*

class ChargeListAdapter(private val navController: NavController,private val items : List<ChargeItem>) : RecyclerView.Adapter<ChargeListAdapter.ChargeListViewHolder>(){

    class ChargeListViewHolder(val binding: ItemChargeBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargeListViewHolder {
        return ChargeListViewHolder(ItemChargeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChargeListViewHolder, position: Int) {
        val chargeItem = items[position]
        holder.binding.apply {

            tvName.text = chargeItem.name
            tvDate.text = DateFormat.getDateInstance().format(chargeItem.timeCreated)
            tvCharge.text = String.format(Locale.ROOT,"%.2f$",chargeItem.value)
            root.setOnClickListener{
                val action = HomeFragmentDirections.actionMiHomeToAddEditItemFragment(chargeItem)
                navController.navigate(action)
            }
        }
    }

    override fun getItemCount(): Int = items.size

}