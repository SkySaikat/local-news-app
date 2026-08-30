package com.chittagong.localnews.data.repository

import com.chittagong.localnews.core.common.FirestoreCollections
import com.chittagong.localnews.core.util.AuthErrorMapper
import com.chittagong.localnews.data.remote.dto.toPostOrNull
import com.chittagong.localnews.di.IoDispatcher
import com.chittagong.localnews.domain.model.Post
import com.chittagong.localnews.domain.repository.AuthRepository
import com.chittagong.localnews.domain.repository.PostRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PostRepository {

    private val postsCollection = firestore.collection(FirestoreCollections.POSTS)

    override fun observePosts(): Flow<Result<List<Post>>> = callbackFlow {
        val registration = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { it.toPostOrNull() }.orEmpty()
                trySend(Result.success(posts))
            }
        awaitClose { registration.remove() }
    }.flowOn(ioDispatcher)

    override fun observePostsByArea(area: String): Flow<Result<List<Post>>> = callbackFlow {
        val registration = postsCollection
            .whereEqualTo("area", area)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { it.toPostOrNull() }.orEmpty()
                trySend(Result.success(posts))
            }
        awaitClose { registration.remove() }
    }.flowOn(ioDispatcher)

    override suspend fun createPost(
        title: String,
        description: String,
        category: String,
        area: String,
        latitude: Double?,
        longitude: Double?,
        imageUrl: String?
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val user = authRepository.currentUser() 
                ?: throw IllegalStateException("Must be signed in to post.")
            
            val postData = mapOf(
                "authorUid" to user.uid,
                "authorName" to (user.displayName ?: "Anonymous"),
                "title" to title,
                "description" to description,
                "category" to category,
                "area" to area,
                "latitude" to latitude,
                "longitude" to longitude,
                "imageUrl" to imageUrl,
                "timestamp" to System.currentTimeMillis(),
                "upvotes" to 0
            )
            postsCollection.add(postData).await()
            Unit
        }.recoverCatching { throwable ->
            throw Exception(AuthErrorMapper.toMessage(throwable), throwable)
        }
    }
}
