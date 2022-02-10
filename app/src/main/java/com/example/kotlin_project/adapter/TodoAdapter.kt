package com.example.kotlin_project.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_project.R
import com.example.kotlin_project.database.Todo

private const val TAG = "TodoAdapter"
class TodoAdapter(val context: Context): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var list = mutableListOf<Todo>()

    inner class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.TodoItem)
        var timestamp = itemView.findViewById<TextView>(R.id.TimeStamp)
        var checkbox = itemView.findViewById<CheckBox>(R.id.cbCheck)

        fun onBind(data: Todo) {
            title.text = data.title
            timestamp.text = data.timestamp
            checkbox.isChecked = data.isChecked

            Log.d(TAG, "onBind: ${checkbox.isChecked}")

            if (data.isChecked) {
                title.paintFlags = title.paintFlags
            } else {
                title.paintFlags = title.paintFlags
            }

            checkbox.setOnClickListener{
                itemCheckBoxClickListener.onClick(it, layoutPosition, list[layoutPosition].id)
            }

            itemView.setOnClickListener {
                itemClickListner.onClick(it, layoutPosition, list[layoutPosition].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo_list, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(newList: MutableList<Todo>) {
        this.list = newList
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onClick(view: View,  position: Int, itemId: Long)
    }

    interface ItemCheckBoxClickListener {
        fun onClick(view: View, position: Int, itemId: Long)
    }

    private lateinit var itemClickListner: ItemClickListener
    private lateinit var itemCheckBoxClickListener: ItemCheckBoxClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    fun setItemCheckBoxClickListener(itemCheckBoxClickListener: ItemCheckBoxClickListener) {
        this.itemCheckBoxClickListener = itemCheckBoxClickListener
    }
}