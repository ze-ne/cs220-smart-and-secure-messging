const functions = require('firebase-functions')
const admin = require('firebase-admin')

const firebase = admin.initializeApp();
const db = admin.firestore();

exports.deleteConversationImages = functions.firestore
    .document('conversations/{conversationId}')
    .onDelete(async (snap, context) => {
        const bucket = firebase.storage().bucket();
        return bucket.deleteFiles({
            prefix: `${context.conversationId}/`
        });
    });
