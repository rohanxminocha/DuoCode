package com.uw.duocode.ui.screens.lessons

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.uw.duocode.data.model.LessonInfo
import com.uw.duocode.data.model.TopicInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class LessonViewModelTest {

    private lateinit var viewModel: LessonViewModel

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockLessonsCollection: CollectionReference

    @Mock
    private lateinit var mockTopicsCollection: CollectionReference

    @Mock
    private lateinit var mockLessonsQuery: Query

    @Mock
    private lateinit var mockLessonTask: Task<QuerySnapshot>

    @Mock
    private lateinit var mockLessonQuerySnapshot: QuerySnapshot

    @Mock
    private lateinit var mockLessonDocument: DocumentSnapshot

    @Mock
    private lateinit var mockTopicDocument: DocumentSnapshot

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = LessonViewModel(mockFirestore)
    }

    @Test
    fun `loadLessonData returns error when lesson not found`() {
        val topicId = "testTopicId"

        `when`(mockFirestore.collection("lessons")).thenReturn(mockLessonsCollection)
        `when`(mockLessonsCollection.whereEqualTo("topicId", topicId)).thenReturn(mockLessonsQuery)
        `when`(mockLessonsQuery.limit(1)).thenReturn(mockLessonsQuery)
        `when`(mockLessonsQuery.get()).thenReturn(mockLessonTask)

        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<QuerySnapshot>>(0)
            `when`(mockLessonQuerySnapshot.isEmpty).thenReturn(true)
            successListener.onSuccess(mockLessonQuerySnapshot)
            mockLessonTask
        }.`when`(mockLessonTask).addOnSuccessListener(any())

        viewModel.loadLessonData(topicId)

        assertEquals("Lesson not found", viewModel.error)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `loadLessonData loads lesson and topic successfully`() {
        val topicId = "testTopicId"

        val testLesson =
            LessonInfo(description = "Test lesson description", imageUrl = "http://test.image.url")
        val testTopic = TopicInfo(name = "Test Topic", iconKey = "testIcon")

        `when`(mockFirestore.collection("lessons")).thenReturn(mockLessonsCollection)
        `when`(mockLessonsCollection.whereEqualTo("topicId", topicId)).thenReturn(mockLessonsQuery)
        `when`(mockLessonsQuery.limit(1)).thenReturn(mockLessonsQuery)
        `when`(mockLessonsQuery.get()).thenReturn(mockLessonTask)

        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<QuerySnapshot>>(0)
            `when`(mockLessonQuerySnapshot.isEmpty).thenReturn(false)
            `when`(mockLessonQuerySnapshot.documents).thenReturn(listOf(mockLessonDocument))
            successListener.onSuccess(mockLessonQuerySnapshot)
            mockLessonTask
        }.`when`(mockLessonTask).addOnSuccessListener(any())

        `when`(mockLessonDocument.toObject(LessonInfo::class.java)).thenReturn(testLesson)

        val mockTopicDocRef = mock(DocumentReference::class.java)
        `when`(mockFirestore.collection("topics")).thenReturn(mockTopicsCollection)
        `when`(mockTopicsCollection.document(topicId)).thenReturn(mockTopicDocRef)
        val mockTopicTask = mock(Task::class.java) as Task<DocumentSnapshot>
        `when`(mockTopicDocRef.get()).thenReturn(mockTopicTask)

        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
            successListener.onSuccess(mockTopicDocument)
            mockTopicTask
        }.`when`(mockTopicTask).addOnSuccessListener(any())

        doAnswer { invocation ->
            mockTopicTask
        }.`when`(mockTopicTask).addOnFailureListener(any())

        `when`(mockTopicDocument.toObject(TopicInfo::class.java)).thenReturn(testTopic)

        viewModel.loadLessonData(topicId)

        assertEquals(testLesson.description, viewModel.description)
        assertEquals(testLesson.imageUrl, viewModel.imageUrl)
        assertEquals(testTopic.name, viewModel.topicName)
        assertEquals(testTopic.iconKey, viewModel.iconKey)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
    }

    @Test
    fun `loadLessonData handles topic loading failure`() {
        val topicId = "testTopicId"
        val testLesson =
            LessonInfo(description = "Test lesson description", imageUrl = "http://test.image.url")

        `when`(mockFirestore.collection("lessons")).thenReturn(mockLessonsCollection)
        `when`(mockLessonsCollection.whereEqualTo("topicId", topicId)).thenReturn(mockLessonsQuery)
        `when`(mockLessonsQuery.limit(1)).thenReturn(mockLessonsQuery)
        `when`(mockLessonsQuery.get()).thenReturn(mockLessonTask)

        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<QuerySnapshot>>(0)
            `when`(mockLessonQuerySnapshot.isEmpty).thenReturn(false)
            `when`(mockLessonQuerySnapshot.documents).thenReturn(listOf(mockLessonDocument))
            successListener.onSuccess(mockLessonQuerySnapshot)
            mockLessonTask
        }.`when`(mockLessonTask).addOnSuccessListener(any())

        `when`(mockLessonDocument.toObject(LessonInfo::class.java)).thenReturn(testLesson)

        val mockTopicDocRef = mock(DocumentReference::class.java)
        `when`(mockFirestore.collection("topics")).thenReturn(mockTopicsCollection)
        `when`(mockTopicsCollection.document(topicId)).thenReturn(mockTopicDocRef)
        val mockTopicTask = mock(Task::class.java) as Task<DocumentSnapshot>
        `when`(mockTopicDocRef.get()).thenReturn(mockTopicTask)

        doAnswer { invocation ->
            val failureListener = invocation.getArgument<OnFailureListener>(0)
            failureListener.onFailure(Exception("Topic error"))
            mockTopicTask
        }.`when`(mockTopicTask).addOnFailureListener(any())

        doAnswer { invocation ->
            mockTopicTask
        }.`when`(mockTopicTask).addOnSuccessListener(any())

        viewModel.loadLessonData(topicId)

        assertEquals("Error loading topic: Topic error", viewModel.error)
        assertFalse(viewModel.isLoading)
    }
}
