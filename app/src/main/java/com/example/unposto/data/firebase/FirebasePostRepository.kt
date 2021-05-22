package com.example.unposto.data.firebase

import com.example.unposto.data.firebase.domain.FirebasePost
import com.google.firebase.firestore.*
import com.google.firebase.storage.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.*
import javax.inject.Inject

class FirebasePostRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
)  {
    private val posts = db.collection("posts")

    suspend fun postsByUser(userId: String, snapshot: QuerySnapshot?): Pair<List<FirebasePost>, QuerySnapshot> {
        val getPostsQuery = posts
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(15)
        val query = if(snapshot == null) getPostsQuery
                    else getPostsQuery.startAt(snapshot)

        val snapshot = query.get() .await()
        return snapshot.toObjects(FirebasePost::class.java) to (snapshot)
    }


    suspend fun createPost(post: FirebasePost): FirebasePost {
        val documentRes = posts.add(post).await()
        
        val res = documentRes.get().await().toObject(FirebasePost::class.java)
        return res!!
    }
}