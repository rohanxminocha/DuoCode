package com.uw.duocode.ui.screens.challenges

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import kotlin.math.exp

class ChallengesViewModelTest {

    private lateinit var viewModel: ChallengesViewModel

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Mock
    private lateinit var mockUsersCollection: CollectionReference

    @Mock
    private lateinit var mockUserDoc: DocumentReference

    @Mock
    private lateinit var mockSubtopicsCollection: CollectionReference

    @Mock
    private lateinit var mockQueryTask: Task<QuerySnapshot>

    @Mock
    private lateinit var mockQuerySnapshot: QuerySnapshot

    private val userId = "test-user-id"

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn(userId)

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
        `when`(mockFirestore.collection("users")).thenReturn(mockUsersCollection)
        `when`(mockUsersCollection.document(userId)).thenReturn(mockUserDoc)
        `when`(mockUserDoc.collection("subtopics")).thenReturn(mockSubtopicsCollection)
        `when`(mockSubtopicsCollection.get()).thenReturn(mockQueryTask)
        `when`(mockQueryTask.addOnSuccessListener(any())).thenReturn(mockQueryTask)
        `when`(mockQueryTask.addOnFailureListener(any())).thenReturn(mockQueryTask)

        viewModel.loadChallenges()

        assertTrue(viewModel.isLoading)
    }

    @Test
    fun `challenges are created correctly from progress`() {
        val testProgressItem = UserSubtopicProgressWithId(
            "1-Dimensional DP",
            UserSubtopicProgress(id = "1dL9iqYFfmv0KjubQN1k", correctAnswers = 10)
        )
        val expectedChallenges = listOf(
            ChallengeData(
                title = "1-Dimensional DP Beginner",
                subTitle = "Finish 2 questions in 1-Dimensional DP",
                completed = false
            ),
            ChallengeData(
                title = "1-Dimensional DP Intermediate",
                subTitle = "Finish 5 questions in 1-Dimensional DP",
                completed = false
            ),
            ChallengeData(
                title = "1-Dimensional DP Expert",
                subTitle = "Finish 10 questions in 1-Dimensional DP",
                completed = false
            )
        )

        val beginnerDoc = Mockito.mock(DocumentSnapshot::class.java)
        `when`(beginnerDoc.toObject(ChallengeData::class.java)).thenReturn(expectedChallenges[0])

        val intermediateDoc = Mockito.mock(DocumentSnapshot::class.java)
        `when`(intermediateDoc.toObject(ChallengeData::class.java)).thenReturn(expectedChallenges[1])

        val expertDoc = Mockito.mock(DocumentSnapshot::class.java)
        `when`(expertDoc.toObject(ChallengeData::class.java)).thenReturn(expectedChallenges[2])

        val challengeMap = mapOf(
            "1-Dimensional DP-Beginner" to beginnerDoc,
            "1-Dimensional DP-Intermediate" to intermediateDoc,
            "1-Dimensional DP-Expert" to expertDoc
        )

        val actualPairs =
            viewModel.createChallengesFromProgress(listOf(testProgressItem), challengeMap)
        val actualChallenges = actualPairs.map { it.second }

        assertEquals(expectedChallenges, actualChallenges)
    }

    @Test
    fun `only 1 new challenge`() {
        val testProgressItem = UserSubtopicProgressWithId(
            "1-Dimensional DP",
            UserSubtopicProgress(id = "1dL9iqYFfmv0KjubQN1k", correctAnswers = 10)
        )
        val beginner = ChallengeData(
            title = "1-Dimensional DP Beginner",
            subTitle = "Finish 2 questions in 1-Dimensional DP",
            completed = true
        )
        val intermediate = ChallengeData(
            title = "1-Dimensional DP Intermediate",
            subTitle = "Finish 5 questions in 1-Dimensional DP",
            completed = true
        )
        val expert = ChallengeData(
            title = "1-Dimensional DP Expert",
            subTitle = "Finish 10 questions in 1-Dimensional DP",
            completed = false
        )

        val beginnerDoc = Mockito.mock(DocumentSnapshot::class.java)
        `when`(beginnerDoc.toObject(ChallengeData::class.java)).thenReturn(beginner)

        val intermediateDoc = Mockito.mock(DocumentSnapshot::class.java)
        `when`(intermediateDoc.toObject(ChallengeData::class.java)).thenReturn(intermediate)

        val expertDoc = Mockito.mock(DocumentSnapshot::class.java)
        `when`(expertDoc.toObject(ChallengeData::class.java)).thenReturn(expert)

        val challengeMap = mapOf(
            "1-Dimensional DP-Beginner" to beginnerDoc,
            "1-Dimensional DP-Intermediate" to intermediateDoc,
            "1-Dimensional DP-Expert" to expertDoc
        )

        val actualPairs =
            viewModel.createChallengesFromProgress(listOf(testProgressItem), challengeMap)
        val actualChallenges = actualPairs.map { it.second }
        val expectedChallenges = listOf(expert)

        assertEquals(expectedChallenges, actualChallenges)
    }
}
