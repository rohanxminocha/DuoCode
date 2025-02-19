package com.uw.duocode.data.model

import com.google.firebase.firestore.DocumentId

enum class Difficulty {
    EASY, MEDIUM, HARD;

    companion object {
        fun fromString(value: String): Difficulty? {
            return try {
                Difficulty.valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

enum class QuestionType {
    MULTIPLE_CHOICE, MATCHING, DRAG_DROP;

    companion object {
        fun fromString(value: String): QuestionType? {
            return try {
                QuestionType.valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

open class Question(
    @DocumentId
    var id: String? = null,
    var subtopicId: String? = null,
    var difficulty: String? = null,
    var questionType: String? = null,
    var description: String? = null,
) {
//    fun getDifficultyEnum(): Difficulty? {
//        return difficulty.let { Difficulty.fromString(it ?: "") }
//    }
//
//    fun getQuestionType(): QuestionType? {
//        return questionType.let { QuestionType.fromString(it ?: "") }
//    }
}

class MultipleChoiceQuestion(
    val options: List<String>? = null,
    val correctAnswer: List<Int>? = null,
    id: String? = null,
    subtopicId: String? = null,
    difficulty: String? = null,
    questionType: String? = null,
    description: String? = null,
) : Question(id, subtopicId, difficulty, questionType, description) {
}

class DragAndDropQuestion(
    val options: List<String>? = null,
    id: String? = null,
    subtopicId: String? = null,
    difficulty: String? = null,
    questionType: String? = null,
    description: String? = null,
) : Question(id, subtopicId, difficulty, questionType, description) {
}

class MatchQuestion(
    val matches: Map<String, String>? = null,
    id: String? = null,
    subtopicId: String? = null,
    difficulty: String? = null,
    questionType: String? = null,
    description: String? = null,
) : Question(id, subtopicId, difficulty, questionType, description) {
}

