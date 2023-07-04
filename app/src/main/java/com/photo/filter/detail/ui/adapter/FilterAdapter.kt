package com.photo.filter.detail.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photo.filter.R
import com.photo.filter.databinding.FilterItemBinding
import com.photo.filter.detail.data.local.model.ImageFilterModel
import com.photo.filter.utills.listener.ImageFilterListener

class FilterAdapter(
    private val imageFilterListener: ImageFilterListener,
    private var imageFilters: List<ImageFilterModel>
) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(imageFilters[position])
    }

    override fun getItemCount() = imageFilters.size

    class FilterViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = FilterItemBinding.bind(item)

        fun bind(imageFilterModel: ImageFilterModel) = with(binding) {
            ivFilterPreview.setImageBitmap(imageFilterModel.filterPreview)
            tvFilterName.text = imageFilterModel.name
        }
    }
}
