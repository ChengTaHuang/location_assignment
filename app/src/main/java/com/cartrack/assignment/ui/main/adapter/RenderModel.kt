package com.cartrack.assignment.ui.main.adapter

import com.cartrack.assignment.data.bean.User

class RenderModel() {
    private var curSelectIndex = 0
    private var users : List<User> = emptyList()
    fun convert(users: List<User>): List<ItemType> {
        this.users = users
        return convertToItemType(users)
    }

    fun updateSelectUser(user : User) : List<ItemType>{
        curSelectIndex = users.indexOf(users.find { it.id == user.id }!!)
        return convertToItemType(users)
    }

    private fun convertToItemType(users: List<User>): List<ItemType>{
        return users.map { it.copy() }.toMutableList().mapIndexed { index, user ->
            if (index == curSelectIndex) {
                ItemType.Select(user)
            } else {
                ItemType.UnSelect(user)
            }
        }
    }
}