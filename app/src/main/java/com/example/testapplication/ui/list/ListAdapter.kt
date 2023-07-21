package com.example.testapplication.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testapplication.R
import com.example.testapplication.data.model.NotificationModel

class ListAdapter(private val callback: (idMeal: Int) -> Unit) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val datas = ArrayList<NotificationModel>()

    fun updateData(newData: ArrayList<NotificationModel>) {
        val diffResult: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(Comparator(datas, newData))

        this.datas.clear()
        this.datas.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_notification_saved, parent, false)
        return ViewHolder(view)
    }

    /*If notifications are get opened,
    * 1. update local to isSeen true
    * 2. update local to isClicked true*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = datas[position]
        holder.txtTitleNotif.text = data.title
        holder.txtBodyNotif.text = data.body
        holder.divNotif.setOnClickListener { callback(data.mealId) }

        Glide.with(holder.itemView)
            .load(data)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.imgAvatar)
    }

    override fun getItemCount(): Int = datas.size


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitleNotif = view.findViewById<TextView>(R.id.txt_title_notif)
        val txtBodyNotif = view.findViewById<TextView>(R.id.txt_body_notif)
        val imgAvatar = view.findViewById<ImageView>(R.id.img_avatar)
        val divNotif = view.findViewById<LinearLayout>(R.id.div_notif)
    }

    inner class Comparator(
        val oldValue: ArrayList<NotificationModel>,
        val newValue: ArrayList<NotificationModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldValue.size

        override fun getNewListSize(): Int = newValue.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldValue[oldItemPosition].id == newValue[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldValue[oldItemPosition].id == newValue[newItemPosition].id

    }
}