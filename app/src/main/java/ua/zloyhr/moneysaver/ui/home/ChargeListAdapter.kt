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
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ua.zloyhr.moneysaver.util.doubleToMoney

import java.util.ArrayList




class ChargeListAdapter(private val navController: NavController) :
    ListAdapter<ChargeItem,ChargeListAdapter.ChargeListViewHolder>(DiffChargeItem()){

    class ChargeListViewHolder(val binding: ItemChargeBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargeListViewHolder {
        return ChargeListViewHolder(ItemChargeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChargeListViewHolder, position: Int) {
        val chargeItem = getItem(position)
        holder.binding.apply {

            tvName.text = chargeItem.name
            tvDate.text = DateFormat.getDateInstance().format(chargeItem.timeCreated)
            tvCharge.text = doubleToMoney(chargeItem.value)
            root.setOnClickListener{
                val action = HomeFragmentDirections.actionMiHomeToAddEditItemFragment(chargeItem,"Edit item")
                navController.navigate(action)
            }
        }
    }

    class DiffChargeItem : DiffUtil.ItemCallback<ChargeItem>(){
        override fun areItemsTheSame(oldItem: ChargeItem, newItem: ChargeItem) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ChargeItem, newItem: ChargeItem) = oldItem.hashCode() == newItem.hashCode()

    }

}