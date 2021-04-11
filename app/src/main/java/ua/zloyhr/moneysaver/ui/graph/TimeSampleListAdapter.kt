package ua.zloyhr.moneysaver.ui.graph

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ua.zloyhr.moneysaver.data.TimeSample
import ua.zloyhr.moneysaver.databinding.ItemChargeBinding
import ua.zloyhr.moneysaver.databinding.ItemTimeSampleBinding
import ua.zloyhr.moneysaver.util.doubleToMoney

class TimeSampleListAdapter : ListAdapter<TimeSample, TimeSampleListAdapter.TimeSampleViewHolder>(DiffTimeSample) {
    class TimeSampleViewHolder(val binding: ItemTimeSampleBinding) :
        RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSampleViewHolder =
        TimeSampleViewHolder(ItemTimeSampleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TimeSampleViewHolder, position: Int) {
        val timeSample = getItem(position)
        holder.binding.apply {
            tvTimeSampleInfo.text = "${doubleToMoney(timeSample.value)} in ${timeSample.periodName}"
        }
    }

    object DiffTimeSample : DiffUtil.ItemCallback<TimeSample>(){
        override fun areItemsTheSame(oldItem: TimeSample, newItem: TimeSample) = oldItem == newItem

        override fun areContentsTheSame(oldItem: TimeSample, newItem: TimeSample) = oldItem == newItem
    }

}