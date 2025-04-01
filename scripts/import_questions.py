import firebase_admin
from firebase_admin import credentials, firestore
import json
from pathlib import Path
import re
from datetime import datetime
import dateutil.parser

cred = credentials.Certificate('./firebase-credentials.json')
firebase_admin.initialize_app(cred)
db = firestore.client()

def get_latest_questions_file():
    pattern = re.compile(r'questions_(\d+)\.json')
    max_version = -1
    latest_file = None

    for file in Path('.').glob('questions_*.json'):
        match = pattern.match(file.name)
        if match:
            version = int(match.group(1))
            if version > max_version:
                max_version = version
                latest_file = file

    if latest_file:
        doc_ref = db.collection('imported_data').document(latest_file.name)
        doc = doc_ref.get()
        if doc.exists:
            raise Exception(
                f"File {latest_file.name} was already imported. "
                "Please use a different version number."
            )

    return latest_file

def upload_questions():
    try:
        latest_file = get_latest_questions_file()

        if not latest_file:
            print("No questions file found (questions_*.json). Nothing to upload.")
            return

        with open(latest_file, 'r') as f:
            questions = json.load(f)

        uploaded_count = 0
        for question in questions:
            if 'releaseDate' in question:
                try:
                    parsed_dt = dateutil.parser.parse(question['releaseDate'])
                    question['releaseDate'] = parsed_dt
                    question['released'] = False
                except Exception as e:
                    print(
                        f"Could not parse releaseDate for question: "
                        f"{question.get('description')}. Error: {e}"
                    )
                    question['releaseDate'] = None
                    question['released'] = True
            else:
                question['releaseDate'] = None
                question['released'] = True

            if 'id' in question:
                db.collection('questions').document(question['id']).set(question)
            else:
                db.collection('questions').add(question)

            uploaded_count += 1

        db.collection('imported_data').document(latest_file.name).set({
            'importedAt': firestore.SERVER_TIMESTAMP,
            'filename': latest_file.name,
            'questionsCount': uploaded_count,
            'collection': 'questions'
        })

        print(f"Successfully imported {uploaded_count} questions into 'questions' from {latest_file}")

    except Exception as e:
        print(f"Error: {str(e)}")

def main():
    print("\n[import_questions] Uploading questions to 'questions' collection...")
    upload_questions()
    print("\n[import_questions] Done!")

if __name__ == '__main__':
    main()
