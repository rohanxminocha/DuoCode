import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import json
import os

cred = credentials.Certificate('./firebase-credentials.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

def export_collection(collection_name):
    docs = db.collection(collection_name).stream()
    
    data = []
    for doc in docs:
        doc_dict = doc.to_dict()
        doc_dict['id'] = doc.id  
        data.append(doc_dict)
    
    os.makedirs('exports', exist_ok=True)
    
    output_file = f'exports/{collection_name}.json'
    with open(output_file, 'w') as f:
        json.dump(data, f, indent=2)
    print(f"Exported {collection_name} to {output_file}")

def main():
    collections = ['questions']
    
    for collection in collections:
        export_collection(collection)

if __name__ == '__main__':
    main()