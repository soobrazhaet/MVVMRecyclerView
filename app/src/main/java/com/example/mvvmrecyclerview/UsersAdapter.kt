package com.example.mvvmrecyclerview

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmrecyclerview.databinding.ItemUserBinding
import com.example.mvvmrecyclerview.model.User
import com.example.mvvmrecyclerview.screens.UserListItem

//интерфейс для работы с действиями пользователя
interface UsersActionListener{
    //перемещение пользователя
    fun onUserMove(user: User, moveBy: Int)

    //удаление пользователя
    fun onUserDelete(user: User)

    //просмотр деталей пользователя
    fun onUserDetails(user: User)

    //для увольнения пользователя
    fun onUserFire(user: User)
}

//класс для работы с обновлением списка
class UserDiffCallback(
    private val oldList: List<User>,
    private val newList: List<User>
): DiffUtil.Callback(){

    //возвращает длину старого списка
    override fun getOldListSize(): Int = oldList.size

    //возвращает длину нового списка
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]

        return oldUser.id == newUser.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]

        return oldUser == newUser
    }
}

class UsersAdapter(
    private val actionListener: UsersActionListener
): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), View.OnClickListener {

    var users: List<UserListItem> = emptyList()
    set(newValue){
        field = newValue
        notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        val user = v.tag as User

        when (v.id){
            R.id.moreImageViewButton ->{
                showPopupMenu(v)
            }
            else ->{
                actionListener.onUserDetails(user)
            }
        }
    }

    //для создания нового элемента списка recyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)

        binding.moreImageViewButton.setOnClickListener(this)
        return UsersViewHolder(binding)
    }

    //для обновления элемента списка
    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val userListItem = users[position]
        val user = userListItem.user
        val context = holder.itemView.context
        with(holder.binding){
            holder.itemView.tag = user
            moreImageViewButton.tag = user

            if (userListItem.isInProgress){
                moreImageViewButton.visibility = View.INVISIBLE
                itemProgressBar.visibility = View.VISIBLE
                holder.binding.root.setOnClickListener(null)
            }
            else{
                moreImageViewButton.visibility = View.VISIBLE
                itemProgressBar.visibility = View.GONE
                holder.binding.root.setOnClickListener(this@UsersAdapter)
            }

            userNameTextView.text = user.name
            userCompanyTextView.text = if (user.company.isNotBlank()) user.company else context.getString(R.string.unemployed)

            if (user.photo.isNotBlank()){
                Glide.with(photoImageView.context)
                    .load(user.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_avatar)
                    .error(R.drawable.ic_user_avatar)
                    .into(photoImageView)
            }
            else{
                photoImageView.setImageResource(R.drawable.ic_user_avatar)
            }
        }
    }

    //для возвращения количества элементов в списке
    override fun getItemCount(): Int = users.size

    private fun showPopupMenu(view: View){
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val user = view.tag as User
        val position = users.indexOfFirst { it.user.id == user.id }

        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
            isEnabled = position < users.size - 1
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.move_remove))

        if (user.company.isNotBlank()){
            popupMenu.menu.add(0, ID_FIRE, Menu.NONE, context.getString(R.string.fire))
        }
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId){
                ID_MOVE_UP -> {
                    actionListener.onUserMove(user, -1)
                }
                ID_MOVE_DOWN ->{
                    actionListener.onUserMove(user, 1)
                }

                ID_REMOVE ->{
                    actionListener.onUserDelete(user)
                }

                ID_FIRE ->{
                    actionListener.onUserFire(user)
                }
            }

            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    class UsersViewHolder(
        val binding: ItemUserBinding
    ): RecyclerView.ViewHolder(binding.root)

    //для хранения идентификаторов действий
    companion object{
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
        private const val ID_FIRE = 4
    }
}