package edu.nku.classapp.model

data class Student(

    val Name: String = "",
    val Dorms: String = "",
    val classYear: Int = 0,
    val Major: String = "",
    val imageURL: List<String> = emptyList(),
    var uid: String = ""
)
