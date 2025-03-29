package com.uw.duocode.ui.screens.challenges

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.uw.duocode.data.model.UserSubtopicProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class ChallengesViewModelTest {
    
    private lateinit var viewModel: ChallengesViewModel
    
    @Mock private lateinit var mockFirestore: FirebaseFirestore
    @Mock private lateinit var mockAuth: FirebaseAuth
    @Mock private lateinit var mockUser: FirebaseUser
    @Mock private lateinit var mockUsersCollection: CollectionReference
    @Mock private lateinit var mockUserDoc: DocumentReference
    @Mock private lateinit var mockSubtopicsCollection: CollectionReference
    @Mock private lateinit var mockQueryTask: Task<QuerySnapshot>
    @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
    
    private val userId = "test-user-id"
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        // Setup Auth mock
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn(userId)
        
        // Initialize view model
        viewModel = ChallengesViewModel(mockAuth, mockFirestore)
    }
    
    @Test
    fun `initial state has empty challenges and no error`() {
        assertTrue(viewModel.challenges.isEmpty())
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
    }
    
    @Test
    fun `loadChallenges sets isLoading to true`() {
        // Setup Firestore mocks for the chain
        `when`(mockFirestore.collection("users")).thenReturn(mockUsersCollection)
        `when`(mockUsersCollection.document(userId)).thenReturn(mockUserDoc)
        `when`(mockUserDoc.collection("subtopics")).thenReturn(mockSubtopicsCollection)
        `when`(mockSubtopicsCollection.get()).thenReturn(mockQueryTask)
        `when`(mockQueryTask.addOnSuccessListener(any())).thenReturn(mockQueryTask)
        `when`(mockQueryTask.addOnFailureListener(any())).thenReturn(mockQueryTask)
        
        // Call the method
        viewModel.loadChallenges()
        
        // Verify loading state is set to true
        assertTrue(viewModel.isLoading)
    }
    
//    @Test
//    fun `challenges are created correctly from progress`() {
//        // Create a test progress item
//        val testProgressItem = UserSubtopicProgress(id = "1dL9iqYFfmv0KjubQN1k", correctAnswers = 10)
//
//        // Create a test challenge manually based on the mapping in the ViewModel
//        val expectedChallenges = listOf(
//            ChallengeData(
//                title = "1-Dimensional DP Beginner",
//                subTitle = "Finish 5 questions in 1-Dimensional DP",
//                completed = true
//            ),
//            ChallengeData(
//                title = "1-Dimensional DP Intermediate",
//                subTitle = "Finish 10 questions in 1-Dimensional DP",
//                completed = true
//            ),
//            ChallengeData(
//                title = "1-Dimensional DP Expert",
//                subTitle = "Finish 15 questions in 1-Dimensional DP",
//                completed = false
//            )
//        )
//
//        // Verify the challenge creation logic works as expected
//        val actualChallenges = viewModel.createChallengesFromProgress(listOf(testProgressItem))
//        assertEquals(expectedChallenges, actualChallenges)
//    }
} 