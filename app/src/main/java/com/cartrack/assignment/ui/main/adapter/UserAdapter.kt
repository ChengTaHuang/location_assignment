package com.cartrack.assignment.ui.main.adapter

import android.os.Parcelable
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
import kotlinx.android.parcel.Parcelize

sealed class ItemType(open val user: User) : Parcelable {
    @Parcelize
    data class Select(override val user: User) : ItemType(user)

    @Parcelize
    data class UnSelect(override val user: User) : ItemType(user)
}

class UserAdapter() : ListAdapter<ItemType, UserAdapter.BaseViewHolder>(TaskDiffCallback()) {
    private val renderModel = RenderModel()
    private var onItemClickListener: ((User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                BaseViewHolder.SelectViewHolder(
                    inflater.inflate(
                        R.layout.item_user,
                        parent,
                        false
                    ),
                    onItemClickListener
                )
            }

            1 -> {
                BaseViewHolder.UnSelectViewHolder(
                    inflater.inflate(
                        R.layout.item_user,
                        parent,
                        false
                    ),
                    onItemClickListener
                )
            }

            else -> throw Exception("NO SUPPORT THIS VIEW TYPE")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is BaseViewHolder.SelectViewHolder -> holder.render(getItem(position).user)
            is BaseViewHolder.UnSelectViewHolder -> holder.render(getItem(position).user)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ItemType.Select -> 0
            is ItemType.UnSelect -> 1
        }
    }

    fun setOnItemClickListener(listener: ((User) -> Unit)) {
        onItemClickListener = listener
    }

    fun update(user: List<User>) {
        submitList(renderModel.convert(user))
    }

    fun updateSelectUser(user: User) {
        submitList(renderModel.updateSelectUser(user))
    }

    sealed class BaseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        protected val imgSelect by lazy { itemView.findViewById<AppCompatImageView>(R.id.imgSelect) }
        protected val clBackground by lazy { itemView.findViewById<TouchConstraintLayout>(R.id.clBackground) }
        private val tvUserName by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvUserName) }
        private val tvPhone by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvPhone) }

        open fun render(user: User) {
            tvUserName.text = user.userName
            tvPhone.text = user.phone
        }

        class SelectViewHolder(private val view: View, private val onItemClickListener: ((User) -> Unit)?) :
            BaseViewHolder(view) {
            override fun render(user: User) {
                super.render(user)
                imgSelect.background = view.context.getDrawable(R.drawable.bg_oval_green)
                clBackground.setOnClickListener {
                }
            }
        }

        class UnSelectViewHolder(private val view: View, private val onItemClickListener: ((User) -> Unit)?) :
            BaseViewHolder(view) {
            override fun render(user: User) {
                super.render(user)
                imgSelect.background = view.context.getDrawable(R.drawable.bg_oval_gary)
                clBackground.setOnClickListener {
                    onItemClickListener?.invoke(user)
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<ItemType>() {
        override fun areItemsTheSame(oldItem: ItemType, newItem: ItemType): Boolean {
            return oldItem::class.simpleName == newItem::class.simpleName
        }

        override fun areContentsTheSame(oldItem: ItemType, newItem: ItemType): Boolean {
            return oldItem.user.id == newItem.user.id
        }
    }
}