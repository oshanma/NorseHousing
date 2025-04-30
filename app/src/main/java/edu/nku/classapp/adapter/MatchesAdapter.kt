package edu.nku.classapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.nku.classapp.R
import edu.nku.classapp.model.Match

class MatchesAdapter(
    private val matches: List<Match>,
    private val onClick: (Match) -> Unit
) : RecyclerView.Adapter<MatchesAdapter.MatchViewHolder>() {

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.matchName)
        val profileImage: ImageView = itemView.findViewById(R.id.matchImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_card_match_view, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.nameText.text = match.name
        Glide.with(holder.profileImage.context)
            .load(match.imageURL)
            .placeholder(R.drawable.ic_profile)
            .into(holder.profileImage)

        holder.itemView.setOnClickListener {
            onClick(match)
        }
    }

    override fun getItemCount(): Int = matches.size
}
