package edu.nku.classapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.nku.classapp.R
import edu.nku.classapp.model.Student

class StudentAdapter(
    private val studentList: List<Student>,
    private val onLikeDislikeClick: (String, Boolean) -> Unit // (uid, true=like, false=dislike)
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.nameTextView)
        val dormText: TextView = view.findViewById(R.id.dormTextView)
        val profileImage: ImageView = view.findViewById(R.id.profileImage)
        val btnLiked: ImageButton = view.findViewById(R.id.btnLiked)
        val btnDisLiked: ImageButton = view.findViewById(R.id.btnDisLiked)

        fun bind(student: Student) {
            nameText.text = "${student.Name} ${student.classYear}"
            dormText.text = student.Dorms

            if (student.imageURL.isNotEmpty()) {
                Glide.with(profileImage.context)
                    .load(student.imageURL[0])
                    .into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // Click listeners for heart and cross
            btnLiked.setOnClickListener {
                onLikeDislikeClick(student.uid, true)
            }

            btnDisLiked.setOnClickListener {
                onLikeDislikeClick(student.uid, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_card_view, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(studentList[position])
    }

    override fun getItemCount(): Int = studentList.size
}
