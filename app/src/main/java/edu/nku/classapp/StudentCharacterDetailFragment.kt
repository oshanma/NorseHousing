package edu.nku.classapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class StudentCharacterDetailFragment : Fragment() {

    private var studentName: String? = null
    private var studentEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studentName = it.getString(ARG_STUDENT_NAME)
            studentEmail = it.getString(ARG_STUDENT_EMAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_list_detail, container, false)

        val emailTextView: TextView = view.findViewById(R.id.student_email_detail)
        val nameTextView: TextView = view.findViewById(R.id.student_name_detail)

        emailTextView.text = studentEmail ?: "Email not provided"
        nameTextView.text = studentName ?: "Name not provided"

        return view
    }

    companion object {
        private const val ARG_STUDENT_NAME = "student_name"
        private const val ARG_STUDENT_EMAIL = "student_email"

        @JvmStatic
        fun newInstance(studentName: String, studentEmail: String) =
            StudentCharacterDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STUDENT_NAME, studentName)
                    putString(ARG_STUDENT_EMAIL, studentEmail)
                }
            }
    }
}
