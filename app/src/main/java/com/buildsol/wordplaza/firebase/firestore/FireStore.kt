package com.buildsol.wordplaza.firebase.firestore

import com.buildsol.wordplaza.model.FollowEdge
import com.buildsol.wordplaza.model.GoogleSignInResult
import com.buildsol.wordplaza.model.PostReaction
import com.buildsol.wordplaza.model.PostUserState
import com.buildsol.wordplaza.model.SavedPost
import com.buildsol.wordplaza.model.UserProfile
import com.buildsol.wordplaza.model.Word
import com.buildsol.wordplaza.model.WordPost
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireStore(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection(USERS_COLLECTION)
    private val wordsCollection = firestore.collection(WORDS_COLLECTION)
    private val postsCollection = firestore.collection(POSTS_COLLECTION)
    private val appMetaDocument = firestore.collection(APP_META_COLLECTION).document(APP_META_DOCUMENT)

    suspend fun initializeUserData(signInResult: GoogleSignInResult): UserProfile {
        val userId = signInResult.firebaseUid.orEmpty()
        require(userId.isNotBlank()) { "Firebase user id is required to initialize user data." }

        val now = System.currentTimeMillis()
        val existingUser = getUserData(userId)
        val displayName = signInResult.displayName.orEmpty().ifBlank { existingUser?.displayName.orEmpty() }
        val userProfile = UserProfile(
            id = userId,
            username = existingUser?.username.orEmpty().ifBlank { usernameFrom(displayName, userId) },
            displayName = displayName,
            email = signInResult.email.orEmpty().ifBlank { existingUser?.email.orEmpty() },
            profilePictureUrl = signInResult.profilePictureUrl.orEmpty()
                .ifBlank { existingUser?.profilePictureUrl.orEmpty() },
            bio = existingUser?.bio.orEmpty(),
            followerCount = existingUser?.followerCount ?: 0,
            followingCount = existingUser?.followingCount ?: 0,
            postCount = existingUser?.postCount ?: existingUser?.postedWordIds?.size ?: 0,
            followingIds = existingUser?.followingIds.orEmpty(),
            followerIds = existingUser?.followerIds.orEmpty(),
            postedWordIds = existingUser?.postedWordIds.orEmpty(),
            createdAt = existingUser?.createdAt?.takeIf { it > 0L } ?: now,
            lastSeen = now
        )

        usersCollection.document(userId)
            .set(userProfile, SetOptions.merge())
            .await()

        return userProfile
    }

    suspend fun getUserData(userId: String): UserProfile? {
        if (userId.isBlank()) return null

        return usersCollection.document(userId)
            .get()
            .await()
            .toObject(UserProfile::class.java)
    }

    suspend fun updateUserData(userProfile: UserProfile) {
        require(userProfile.id.isNotBlank()) { "User id is required to update user data." }

        usersCollection.document(userProfile.id)
            .set(userProfile, SetOptions.merge())
            .await()
    }

    suspend fun updateUserProfile(userId: String, displayName: String, profilePictureUrl: String) {
        require(userId.isNotBlank()) { "User id is required to update profile." }

        usersCollection.document(userId)
            .update(
                mapOf(
                    DISPLAY_NAME_FIELD to displayName,
                    PROFILE_PICTURE_URL_FIELD to profilePictureUrl,
                    USERNAME_FIELD to usernameFrom(displayName, userId)
                )
            )
            .await()
    }

    suspend fun updateLastSeen(userId: String) {
        if (userId.isBlank()) return

        usersCollection.document(userId)
            .set(mapOf(LAST_SEEN_FIELD to System.currentTimeMillis()), SetOptions.merge())
            .await()
    }

    suspend fun publishWordPost(userId: String, word: Word, caption: String = ""): WordPost {
        require(userId.isNotBlank()) { "User id is required to publish a post." }
        require(word.word.isNotBlank()) { "Word is required to publish a post." }
        require(word.meaning.isNotBlank()) { "Meaning is required to publish a post." }

        val author = getUserData(userId) ?: error("User profile must exist before publishing posts.")
        val canonicalWord = createOrReuseWord(userId, word)
        val now = System.currentTimeMillis()
        val postDocument = postsCollection.document()
        val post = WordPost(
            id = postDocument.id,
            wordId = canonicalWord.id,
            authorId = userId,
            authorUsername = author.username,
            authorDisplayName = author.displayName,
            authorProfilePictureUrl = author.profilePictureUrl,
            word = canonicalWord.word,
            normalizedWord = canonicalWord.normalizedWord,
            meaning = canonicalWord.meaning,
            pronunciation = canonicalWord.pronunciation,
            synonyms = canonicalWord.synonyms,
            antonyms = canonicalWord.antonyms,
            example = word.egUse.ifBlank { canonicalWord.egUse },
            caption = caption.trim(),
            createdAt = now,
            updatedAt = now
        )

        val batch = firestore.batch()
        batch.set(postDocument, post)
        batch.set(
            wordsCollection.document(canonicalWord.id),
            mapOf(
                POST_COUNT_FIELD to FieldValue.increment(1),
                UPDATED_AT_FIELD to now
            ),
            SetOptions.merge()
        )
        batch.set(
            usersCollection.document(userId),
            mapOf(
                POST_COUNT_FIELD to FieldValue.increment(1),
                POSTED_WORD_IDS_FIELD to FieldValue.arrayUnion(canonicalWord.id),
                LAST_SEEN_FIELD to now
            ),
            SetOptions.merge()
        )
        batch.commit().await()

        return post
    }

    suspend fun createOrReuseWord(userId: String, word: Word): Word {
        val normalizedWord = normalizeWord(word.word)
        require(normalizedWord.isNotBlank()) { "Word must contain letters or numbers." }

        val wordDocument = wordsCollection.document(normalizedWord)
        val existingWord = wordDocument.get().await().toObject(Word::class.java)
        if (existingWord != null && existingWord.id.isNotBlank()) {
            return existingWord
        }

        val now = System.currentTimeMillis()
        val wordToSave = word.copy(
            id = wordDocument.id,
            normalizedWord = normalizedWord,
            examples = word.examples.ifEmpty { listOfNotBlank(word.egUse) },
            createdByUserId = userId,
            createdAt = now,
            updatedAt = now
        )
        wordDocument.set(wordToSave, SetOptions.merge()).await()

        return wordToSave
    }

    suspend fun loadFeedPosts(limit: Long = DEFAULT_FEED_LIMIT): List<WordPost> {
        return postsCollection
            .whereEqualTo(VISIBILITY_FIELD, WordPost.VISIBILITY_PUBLIC)
            .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
            .toObjects(WordPost::class.java)
    }

    suspend fun loadProfilePosts(authorId: String, limit: Long = DEFAULT_FEED_LIMIT): List<WordPost> {
        if (authorId.isBlank()) return emptyList()

        return postsCollection
            .whereEqualTo(AUTHOR_ID_FIELD, authorId)
            .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
            .toObjects(WordPost::class.java)
    }

    suspend fun setPostReaction(userId: String, postId: String, type: String): PostUserState {
        require(userId.isNotBlank()) { "User id is required to react to a post." }
        require(postId.isNotBlank()) { "Post id is required to react to a post." }
        require(type == PostReaction.TYPE_LIKE || type == PostReaction.TYPE_DISLIKE) {
            "Reaction type must be like or dislike."
        }

        val postReference = postsCollection.document(postId)
        val reactionReference = postReference.collection(REACTIONS_COLLECTION).document(userId)

        return firestore.runTransaction { transaction ->
            val reactionSnapshot = transaction.get(reactionReference)
            val existingType = reactionSnapshot.getString(TYPE_FIELD)
            val now = System.currentTimeMillis()

            when {
                existingType == type -> {
                    transaction.delete(reactionReference)
                    transaction.update(postReference, reactionCountField(type), FieldValue.increment(-1))
                    PostUserState(postId = postId)
                }

                existingType == null -> {
                    transaction.set(
                        reactionReference,
                        PostReaction(userId = userId, postId = postId, type = type, createdAt = now, updatedAt = now)
                    )
                    transaction.update(postReference, reactionCountField(type), FieldValue.increment(1))
                    PostUserState(
                        postId = postId,
                        liked = type == PostReaction.TYPE_LIKE,
                        disliked = type == PostReaction.TYPE_DISLIKE
                    )
                }

                else -> {
                    transaction.set(
                        reactionReference,
                        mapOf(TYPE_FIELD to type, UPDATED_AT_FIELD to now),
                        SetOptions.merge()
                    )
                    transaction.update(postReference, reactionCountField(existingType), FieldValue.increment(-1))
                    transaction.update(postReference, reactionCountField(type), FieldValue.increment(1))
                    PostUserState(
                        postId = postId,
                        liked = type == PostReaction.TYPE_LIKE,
                        disliked = type == PostReaction.TYPE_DISLIKE
                    )
                }
            }
        }.await()
    }

    suspend fun removePostReaction(userId: String, postId: String): PostUserState {
        require(userId.isNotBlank()) { "User id is required to remove a reaction." }
        require(postId.isNotBlank()) { "Post id is required to remove a reaction." }

        val postReference = postsCollection.document(postId)
        val reactionReference = postReference.collection(REACTIONS_COLLECTION).document(userId)

        return firestore.runTransaction { transaction ->
            val reactionSnapshot = transaction.get(reactionReference)
            val existingType = reactionSnapshot.getString(TYPE_FIELD)
            if (existingType != null) {
                transaction.delete(reactionReference)
                transaction.update(postReference, reactionCountField(existingType), FieldValue.increment(-1))
            }
            PostUserState(postId = postId)
        }.await()
    }

    suspend fun toggleSavedPost(userId: String, postId: String): Boolean {
        require(userId.isNotBlank()) { "User id is required to save a post." }
        require(postId.isNotBlank()) { "Post id is required to save." }

        val postReference = postsCollection.document(postId)
        val savedReference = usersCollection.document(userId)
            .collection(SAVED_POSTS_COLLECTION)
            .document(postId)

        return firestore.runTransaction { transaction ->
            val savedSnapshot = transaction.get(savedReference)
            if (savedSnapshot.exists()) {
                transaction.delete(savedReference)
                transaction.update(postReference, SAVE_COUNT_FIELD, FieldValue.increment(-1))
                false
            } else {
                transaction.set(
                    savedReference,
                    SavedPost(userId = userId, postId = postId, savedAt = System.currentTimeMillis())
                )
                transaction.update(postReference, SAVE_COUNT_FIELD, FieldValue.increment(1))
                true
            }
        }.await()
    }

    suspend fun getCurrentUserPostStates(userId: String, postIds: List<String>): Map<String, PostUserState> {
        if (userId.isBlank() || postIds.isEmpty()) return emptyMap()

        return postIds.distinct().associateWith { postId ->
            val reaction = postsCollection.document(postId)
                .collection(REACTIONS_COLLECTION)
                .document(userId)
                .get()
                .await()
                .getString(TYPE_FIELD)
            val saved = usersCollection.document(userId)
                .collection(SAVED_POSTS_COLLECTION)
                .document(postId)
                .get()
                .await()
                .exists()

            PostUserState(
                postId = postId,
                liked = reaction == PostReaction.TYPE_LIKE,
                disliked = reaction == PostReaction.TYPE_DISLIKE,
                saved = saved
            )
        }
    }

    suspend fun followUser(currentUserId: String, targetUserId: String) {
        require(currentUserId.isNotBlank()) { "Current user id is required." }
        require(targetUserId.isNotBlank()) { "Target user id is required." }
        require(currentUserId != targetUserId) { "Users cannot follow themselves." }

        val followingReference = usersCollection.document(currentUserId)
            .collection(FOLLOWING_COLLECTION)
            .document(targetUserId)
        val followerReference = usersCollection.document(targetUserId)
            .collection(FOLLOWERS_COLLECTION)
            .document(currentUserId)
        val currentUserReference = usersCollection.document(currentUserId)
        val targetUserReference = usersCollection.document(targetUserId)

        firestore.runTransaction { transaction ->
            if (!transaction.get(followingReference).exists()) {
                val now = System.currentTimeMillis()
                transaction.set(followingReference, FollowEdge(currentUserId, targetUserId, now))
                transaction.set(followerReference, FollowEdge(targetUserId, currentUserId, now))
                transaction.update(currentUserReference, FOLLOWING_COUNT_FIELD, FieldValue.increment(1))
                transaction.update(targetUserReference, FOLLOWER_COUNT_FIELD, FieldValue.increment(1))
            }
            null
        }.await()
    }

    suspend fun unfollowUser(currentUserId: String, targetUserId: String) {
        require(currentUserId.isNotBlank()) { "Current user id is required." }
        require(targetUserId.isNotBlank()) { "Target user id is required." }
        require(currentUserId != targetUserId) { "Users cannot unfollow themselves." }

        val followingReference = usersCollection.document(currentUserId)
            .collection(FOLLOWING_COLLECTION)
            .document(targetUserId)
        val followerReference = usersCollection.document(targetUserId)
            .collection(FOLLOWERS_COLLECTION)
            .document(currentUserId)
        val currentUserReference = usersCollection.document(currentUserId)
        val targetUserReference = usersCollection.document(targetUserId)

        firestore.runTransaction { transaction ->
            if (transaction.get(followingReference).exists()) {
                transaction.delete(followingReference)
                transaction.delete(followerReference)
                transaction.update(currentUserReference, FOLLOWING_COUNT_FIELD, FieldValue.increment(-1))
                transaction.update(targetUserReference, FOLLOWER_COUNT_FIELD, FieldValue.increment(-1))
            }
            null
        }.await()
    }

    suspend fun addToFollowingList(currentUserId: String, followingUserId: String) {
        followUser(currentUserId, followingUserId)
    }

    suspend fun showFollower(userId: String): List<UserProfile> {
        if (userId.isBlank()) return emptyList()

        val followerIds = usersCollection.document(userId)
            .collection(FOLLOWERS_COLLECTION)
            .get()
            .await()
            .documents
            .map { it.id }

        return getUsersByIds(followerIds.ifEmpty { getUserData(userId)?.followerIds.orEmpty() })
    }

    suspend fun getFollowingUsers(userId: String): List<UserProfile> {
        if (userId.isBlank()) return emptyList()

        val followingIds = usersCollection.document(userId)
            .collection(FOLLOWING_COLLECTION)
            .get()
            .await()
            .documents
            .map { it.id }

        return getUsersByIds(followingIds.ifEmpty { getUserData(userId)?.followingIds.orEmpty() })
    }

    suspend fun showPostedWordListId(userId: String): List<String> {
        val posts = loadProfilePosts(userId)
        return posts.map { it.wordId }.ifEmpty { getUserData(userId)?.postedWordIds.orEmpty() }
    }

    suspend fun addWordToWordList(userId: String, word: Word): String {
        return publishWordPost(userId, word).wordId
    }

    suspend fun addWordOfTheDay(wordId: String) {
        require(wordId.isNotBlank()) { "Word id is required to set word of the day." }

        appMetaDocument
            .set(
                mapOf(
                    WORD_OF_THE_DAY_ID_FIELD to wordId,
                    UPDATED_AT_FIELD to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
            .await()
    }

    suspend fun getWordOfTheDay(): Word? {
        val wordId = appMetaDocument
            .get()
            .await()
            .getString(WORD_OF_THE_DAY_ID_FIELD)
            .orEmpty()

        return getWordById(wordId)
    }

    suspend fun getWordByName(wordName: String): Word? {
        val normalizedWord = normalizeWord(wordName)
        if (normalizedWord.isBlank()) return null

        return wordsCollection.document(normalizedWord)
            .get()
            .await()
            .toObject(Word::class.java)
            ?: wordsCollection
                .whereEqualTo(WORD_FIELD, wordName.trim())
                .limit(1)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.toObject(Word::class.java)
    }

    suspend fun getWordById(wordId: String): Word? {
        if (wordId.isBlank()) return null

        return wordsCollection.document(wordId)
            .get()
            .await()
            .toObject(Word::class.java)
    }

    private suspend fun getUsersByIds(userIds: List<String>): List<UserProfile> {
        if (userIds.isEmpty()) return emptyList()

        return userIds.distinct().mapNotNull { userId ->
            getUserData(userId)
        }
    }

    private fun reactionCountField(type: String): String {
        return when (type) {
            PostReaction.TYPE_LIKE -> LIKE_COUNT_FIELD
            PostReaction.TYPE_DISLIKE -> DISLIKE_COUNT_FIELD
            else -> error("Unsupported reaction type: $type")
        }
    }

    private fun normalizeWord(value: String): String {
        return value.trim()
            .lowercase(Locale.US)
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
    }

    private fun usernameFrom(displayName: String, userId: String): String {
        val base = displayName.trim()
            .lowercase(Locale.US)
            .replace(Regex("[^a-z0-9]+"), ".")
            .trim('.')
        return (base.ifBlank { "user" } + "." + userId.take(6)).trim('.')
    }

    private fun listOfNotBlank(value: String): List<String> {
        return if (value.isBlank()) emptyList() else listOf(value)
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }

    companion object {
        private const val DEFAULT_FEED_LIMIT = 30L

        private const val USERS_COLLECTION = "users"
        private const val WORDS_COLLECTION = "words"
        private const val POSTS_COLLECTION = "posts"
        private const val REACTIONS_COLLECTION = "reactions"
        private const val SAVED_POSTS_COLLECTION = "savedPosts"
        private const val FOLLOWING_COLLECTION = "following"
        private const val FOLLOWERS_COLLECTION = "followers"
        private const val APP_META_COLLECTION = "appMeta"
        private const val APP_META_DOCUMENT = "wordPlaza"

        private const val AUTHOR_ID_FIELD = "authorId"
        private const val CREATED_AT_FIELD = "createdAt"
        private const val DISLIKE_COUNT_FIELD = "dislikeCount"
        private const val DISPLAY_NAME_FIELD = "displayName"
        private const val FOLLOWER_COUNT_FIELD = "followerCount"
        private const val FOLLOWING_COUNT_FIELD = "followingCount"
        private const val LAST_SEEN_FIELD = "lastSeen"
        private const val LIKE_COUNT_FIELD = "likeCount"
        private const val POST_COUNT_FIELD = "postCount"
        private const val POSTED_WORD_IDS_FIELD = "postedWordIds"
        private const val PROFILE_PICTURE_URL_FIELD = "profilePictureUrl"
        private const val SAVE_COUNT_FIELD = "saveCount"
        private const val TYPE_FIELD = "type"
        private const val UPDATED_AT_FIELD = "updatedAt"
        private const val USERNAME_FIELD = "username"
        private const val VISIBILITY_FIELD = "visibility"
        private const val WORD_FIELD = "word"
        private const val WORD_OF_THE_DAY_ID_FIELD = "wordOfTheDayId"
    }
}
