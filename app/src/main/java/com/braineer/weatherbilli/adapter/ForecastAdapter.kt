package com.braineer.weatherbilli.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.braineer.weatherbilli.databinding.ForecastRowNewBinding
import com.braineer.weatherbilli.models.ForecastModel
import java.util.*

class ForecastAdapter : ListAdapter<ForecastModel.ForecastItem, ForecastAdapter.ForecastViewHolder>(
    ForecastDiffUtil()
){

    class ForecastViewHolder(val binding: ForecastRowNewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ForecastModel.ForecastItem) {
            binding.item = item
        }
    }

    class ForecastDiffUtil : DiffUtil.ItemCallback<ForecastModel.ForecastItem>() {
        override fun areItemsTheSame(
            oldItem: ForecastModel.ForecastItem,
            newItem: ForecastModel.ForecastItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ForecastModel.ForecastItem,
            newItem: ForecastModel.ForecastItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ForecastRowNewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        //set card background

        if (position%8 == 0) {
            holder.binding.cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ff5353"))
        } else if (position%8 == 1) {
            holder.binding.cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3A2C60"))
        } else if (position%8 == 2) {
            holder.binding.cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5396e4"))
        } else if (position%8 == 3) {
            holder.binding.cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2c66f7"))
        } else if (position%8 == 4) {
            holder.binding.cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#fc4041"))
        } else if (position%8 == 5) {
            holder.binding.cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#c96557"))
        } else if (position%8 == 6) {
            holder.binding.cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#37b7be"))
        } else if (position%8 == 7) {
            holder.binding.cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ed3c56"))
        }

    }
}