package com.cartrack.assignment.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cartrack.assignment.R
import com.cartrack.assignment.data.bean.User
import com.cartrack.assignment.ui.custom.TouchConstraintLayout

class UserAdapter(private val recyclerView: RecyclerView) :
    ListAdapter<User, UserAdapter.BaseViewHolder>(TaskDiffCallback()) {
    private var onItemClickListener: ((User) -> Unit)? = null
    private var selectItemData = mutableListOf<Boolean>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BaseViewHolder(
            inflater.inflate(
                R.layout.item_user,
                parent,
                false
            ),
            onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.render(getItem(position), position)
        holder.renderSelect(selectItemData[position])
    }

    fun setOnItemClickListener(listener: ((User) -> Unit)) {
        onItemClickListener = listener
    }

    fun update(user: List<User>) {
        initSelectItemData(user.size)
        submitList(user)
    }

    fun updateSelectUser(user: User) {
        if(selectItemData.contains(true)) {
            selectItemData[selectItemData.indexOf(true)] = false
        }
        selectItemData[currentList.indexOf(user)] = true
        reRenderSelectItem(selectItemData)
    }

    private fun initSelectItemData(size: Int) {
        for (x in 0..size) {
            selectItemData.add(false)
        }
    }

    private fun reRenderSelectItem(selectItemData: List<Boolean>) {
        val x = recyclerView.childCount
        var i = 0
        while (i < x) {
            val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i))
            if (holder is BaseViewHolder) {
                holder.renderSelect(selectItemData[holder.itemView.tag as Int])
            }
            ++i
        }
    }

    class BaseViewHolder(itemView: View, private val onItemClickListener: ((User) -> Unit)?) :
        RecyclerView.ViewHolder(itemView) {
        private val imgSelect by lazy { itemView.findViewById<AppCompatImageView>(R.id.imgSelect) }
        private val clBackground by lazy { itemView.findViewById<TouchConstraintLayout>(R.id.clBackground) }
        private val tvUserName by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvUserName) }
        private val tvPhone by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvPhone) }
        fun render(user: User, position: Int) {
            itemView.tag = position
            tvUserName.text = user.userName
            tvPhone.text = user.phone
            clBackground.setOnClickListener {
                onItemClickListener?.invoke(user)
            }
        }

        fun renderSelect(isSelect: Boolean) {
            imgSelect.background = if (isSelect) itemView.context.getDrawable(R.drawable.bg_oval_green)
            else itemView.context.getDrawable(R.drawable.bg_oval_gary)
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}