package edu.nku.classapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import edu.nku.classapp.adapter.StudentAdapter
import edu.nku.classapp.model.Student

class studentListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: TextView

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val studentList = mutableListOf<Student>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_list, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        errorMessage = view.findViewById(R.id.error_message)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = StudentAdapter(studentList) { uid, liked ->
            handleLikeDislike(uid, liked)
        }

        loadStudents()

        return view
    }

    private fun loadStudents() {
        progressBar.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE

        firestore.collection("users").get()
            .addOnSuccessListener { result ->
                studentList.clear()
                for (doc in result) {
                    val student = doc.toObject(Student::class.java)
                    student.uid = doc.id  // important: capture the UID
                    studentList.add(student)
                }
                recyclerView.adapter?.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
                errorMessage.text = "Failed to load students: ${it.message}"
            }
    }

    private fun handleLikeDislike(targetUid: String, liked: Boolean) {
        val currentUid = auth.currentUser?.uid ?: return

        val field = if (liked) "liked" else "disliked"

        firestore.collection("users").document(currentUid)
            .update(field, FieldValue.arrayUnion(targetUid))
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    if (liked) "Liked!" else "Disliked!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
