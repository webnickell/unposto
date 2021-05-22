package com.example.unposto.data.firebase

import android.net.Uri
import com.example.unposto.data.firebase.domain.FirebasePost
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirebaseFileRepository @Inject constructor (
        private val storage: FirebaseStorage,
) {
    private val imageStorageRef = storage.reference.child("images")

    suspend fun saveImage(imageUri: Uri): Uri {
        val extension = imageUri.path?.substringAfterLast('.', "")

        val uuid = UUID.randomUUID().toString();
        val imageId = "$uuid.$extension"
        val fileRes = imageStorageRef.child(imageId).putFile(imageUri).await()

        val resUri = fileRes.storage.downloadUrl.await()

        return resUri
    }
}