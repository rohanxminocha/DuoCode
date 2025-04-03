package com.uw.duocode.ui.utils

import com.uw.duocode.data.model.TopicInfo
import com.uw.duocode.data.model.SubtopicInfo
import com.uw.duocode.data.model.UserSubtopicProgress
import com.uw.duocode.ui.screens.profile.AchievementsViewModel

fun checkAndUnlockAchievements(
    topics: List<TopicInfo>,
    subtopics: List<SubtopicInfo>,
    userProgress: List<UserSubtopicProgress>,
    achievementsViewModel: AchievementsViewModel
) {
    topics.forEach { topic ->
        val topicSubtopics = subtopics.filter { it.topicId == topic.id }
        val totalSubtopics = topicSubtopics.size

        val completedCount = topicSubtopics.count { subtopic ->
            userProgress.find { it.id == subtopic.id }?.completed == true
        }

        if (totalSubtopics > 0 && completedCount == totalSubtopics) {
            achievementsViewModel.unlockAchievement(topic.id)
        }
    }
}