package com.example.testapplication.ui.list

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.testapplication.R
import com.example.testapplication.data.model.NotificationModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class ListAdapter(context: Context, private val callback: (idMeal: Int) -> Unit) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val datas = mutableListOf<NotificationModel>()
    private var glideInstance: RequestManager

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ListAdapterEntryPoint{
        fun getGlideInstance(): RequestManager
    }
    init {
        val entryPoint = EntryPointAccessors.fromApplication(context, ListAdapterEntryPoint::class.java)
        glideInstance = entryPoint.getGlideInstance()
    }

    fun updateData(newData: MutableList<NotificationModel>) {
        val diffResult: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(Comparator(datas, newData))

        diffResult.dispatchUpdatesTo(this)
        this.datas.clear()
        this.datas.addAll(newData)
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
        holder.txtTitleNotif.text = data.titleFormatted
        holder.txtBodyNotif.text = data.bodyFormatted
        holder.divNotif.setOnClickListener { callback(data.mealId) }
        holder.txtStatus.text = if(data.isClicked) "clicked" else "seen"
        holder.txtStatus.setTextColor(if(data.isClicked) Color.BLUE else Color.DKGRAY)
        if(data.isSeen) holder.divNotif.setBackgroundColor(Color.parseColor("#dadada"))

        glideInstance.load(
            if (data.id == 0) R.drawable.dummy else data.img_remote
        ).into(holder.imgAvatar)
    }

    override fun getItemCount(): Int = datas.size


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitleNotif = view.findViewById<TextView>(R.id.txt_title_notif)
        val txtBodyNotif = view.findViewById<TextView>(R.id.txt_body_notif)
        val txtStatus = view.findViewById<TextView>(R.id.txt_status)
        val imgAvatar = view.findViewById<ImageView>(R.id.img_avatar)
        val divNotif = view.findViewById<ConstraintLayout>(R.id.div_notif)
    }

    inner class Comparator(
        val oldValue: MutableList<NotificationModel>,
        val newValue: MutableList<NotificationModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldValue.size

        override fun getNewListSize(): Int = newValue.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldValue[oldItemPosition].id == newValue[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldValue[oldItemPosition].id == newValue[newItemPosition].id

    }
}