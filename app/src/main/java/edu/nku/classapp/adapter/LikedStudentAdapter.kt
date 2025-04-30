package edu.nku.classapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.nku.classapp.R

class LikedStudentAdapter(
    private val context: Context,
    private val userList: List<Pair<String, String>> 
) : RecyclerView.Adapter<LikedStudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageProfile: ImageView = view.findViewById(R.id.imageProfile)
        val tvName: TextView = view.findViewById(R.id.tvUserName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.student_card_liked_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (imageUrl, name) = userList[position]

        holder.tvName.text = name
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imageProfile)
    }

    override fun getItemCount(): Int = userList.size
}
