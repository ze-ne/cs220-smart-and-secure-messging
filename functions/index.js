const functions = require('firebase-functions');
const admin = require('firebase-admin');
const { Storage } = require('@google-cloud/storage');

const firebase = admin.initializeApp();
const storage = new Storage();
const bucket = storage.bucket('private-chat-client.appspot.com');

exports.deleteConversationImages = functions.firestore
    .document('conversations/{conversationId}')
    .onDelete(async (snap, context) => {
        return bucket.deleteFiles({
            prefix: `${context.params.conversationId}/`
        });
    });
