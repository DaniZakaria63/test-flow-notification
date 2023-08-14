package com.example.testapplication.ui.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.R

class IngredientAdapter : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {
    private val ingredients = mutableListOf<Pair<String, String>>()

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val text1 = item.findViewById<TextView>(R.id.text1)
        val text2 = item.findViewById<TextView>(R.id.text2)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<Pair<String, String>>) {
        this.ingredients.clear()
        this.ingredients.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_ingredients, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = ingredients.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (ingredient, value) = ingredients[position]
        holder.text1.text = ingredient
        holder.text2.text = value
    }
}