package com.skjo_wiz.todolist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class MainViewModel : ViewModel() {
    val db = FirebaseFirestore.getInstance()

    val todoLiveData = MutableLiveData<List<DocumentSnapshot>>()

    private val data = arrayListOf<QueryDocumentSnapshot>()

    init {
        fetchData()
    }

    fun fetchData() {
        Log.d("TAG", "Init Start")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            db.collection(user.uid)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        todoLiveData.value = value.documents
                    }
                }
        }
    }

    fun toggleTodo(todo: DocumentSnapshot) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val isDone = todo.getBoolean("isDone") ?: false
            db.collection(user.uid).document(todo.id).update("isDone", !isDone)
        }
    }

    fun addTodo(todo: Todo) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            db.collection(user.uid).add(todo)
        }
    }

    fun deleteTodo(todo: DocumentSnapshot) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            db.collection(user.uid).document(todo.id).delete()
        }
    }
}