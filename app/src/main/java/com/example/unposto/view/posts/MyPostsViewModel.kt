package com.example.unposto.view.posts

import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.example.unposto.data.firebase.FirebasePostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPostsViewModel @Inject constructor(
        private val auth: FirebaseAuth,
        private val postRepository: FirebasePostRepository,
) : ViewModel() {
    private val TAG = "MyPostsViewModel: "
    private var querySnapshot: QuerySnapshot? = null
    private val loadParam = MutableLiveData<Boolean>()
    val posts = loadParam.switchMap { reload ->
        liveData {
            val userId = auth.currentUser?.uid!!
            val result = postRepository.postsByUser(
                userId,
                if(reload) null else querySnapshot,
            )
            querySnapshot = result.second
            emit(result.first)
        }
    }
    fun loadPosts(reload: Boolean = false) = loadParam.postValue(reload)
}