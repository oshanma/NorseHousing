package edu.nku.classapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import edu.nku.classapp.adapter.StudentAdapter
import edu.nku.classapp.model.Student

class studentListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: TextView
    private val firestore = FirebaseFirestore.getInstance()
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
        recyclerView.adapter = StudentAdapter(studentList)

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
}
