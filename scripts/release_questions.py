import firebase_admin
from firebase_admin import credentials, firestore, messaging
import datetime
from google.cloud.firestore_v1.field_path import FieldPath

cred = credentials.Certificate('./firebase-credentials.json')
firebase_admin.initialize_app(cred)
db = firestore.client()

def release_new_questions():
    now = datetime.datetime.now(datetime.timezone.utc)
    query = (
        db.collection("questions")
          .where("released", "==", False)
          .where("releaseDate", "<=", now)
    )
    docs = list(query.stream())

    if not docs:
        print("No new questions to release at this time.")
        return

    subtopic_ids = set()
    for d in docs:
        d.reference.update({"released": True})
        question_data = d.to_dict()
        if "subtopicId" in question_data:
            subtopic_ids.add(question_data["subtopicId"])

    print(f"Released {len(docs)} questions.")

    if subtopic_ids:
        update_users_with_new_subtopics(list(subtopic_ids))

def update_users_with_new_subtopics(subtopic_ids):
    users = db.collection("users").stream()
    count = 0
    for user_snapshot in users:
        user_snapshot.reference.update({
            "newQuestionsAvailable": True,
            "newQuestionsSubtopics": firestore.ArrayUnion(subtopic_ids)
        })
        count += 1
    print(f"Updated {count} users with newQuestionsSubtopics={subtopic_ids}.")

def main():
    print("[release_questions] Checking for newly releaseable questions in 'questions_test' ...")
    release_new_questions_test()
    print("[release_questions] Done!")

if __name__ == '__main__':
    main()
