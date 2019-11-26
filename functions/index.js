const functions = require('firebase-functions')
const admin = require('firebase-admin')

const firebase = admin.initializeApp();
// const db = admin.firestore();

exports.deleteConversationImages = functions.firestore
    .document('conversations/{conversationId}')
    .onDelete(async (snap, context) => {
        const folderPath = `${context.conversationId}`;
        const ref = firebase.storage().ref(folderPath);
        return ref.listAll().then(dir => {
            dir.items.forEach(fileRef => {
                this.deleteFile(ref.fullPath, fileRef.name);
            });
            return;
        });
    });
