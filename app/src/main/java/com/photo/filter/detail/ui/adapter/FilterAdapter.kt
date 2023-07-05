package com.photo.filter.detail.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photo.filter.R
import com.photo.filter.databinding.FilterItemBinding
import com.photo.filter.detail.data.local.model.ImageFilterModel

class FilterAdapter(
    private var imageFilters: List<ImageFilterModel>,
    private val imageFilterListener: (ImageFilterModel) -> Unit
) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        return FilterViewHolder(view, imageFilterListener)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(imageFilters[position])
    }

    override fun getItemCount() = imageFilters.size

    class FilterViewHolder(
        item: View,
        private val imageFilterListener: (ImageFilterModel) -> Unit,
    ) : RecyclerView.ViewHolder(item) {
        private val binding = FilterItemBinding.bind(item)

        fun bind(imageFilterModel: ImageFilterModel) = with(binding) {
            ivFilterPreview.setImageBitmap(imageFilterModel.filterPreview)
            tvFilterName.text = imageFilterModel.name
            root.setOnClickListener {
                imageFilterListener.invoke(imageFilterModel)

            }
        }
    }
}
