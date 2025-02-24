import firebase_admin
from firebase_admin import credentials, firestore
import json
import os
from pathlib import Path
import re

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
    
    return latest_file

def upload_questions():
    latest_file = get_latest_questions_file()
    
    if not latest_file:
        print("No questions file found")
        return
        
    with open(latest_file, 'r') as f:
        questions = json.load(f)
    
    for question in questions:
        if 'id' in question:
            db.collection('questions').document(question['id']).set(question)
        else:
            db.collection('questions').add(question)
    
    print(f"Uploaded questions from {latest_file}")

def main():
    print("\nUploading questions")
    upload_questions()
    
    print("\nDone!")

if __name__ == '__main__':
    main()