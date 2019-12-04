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

exports.notifyNewMessages = functions.firestore
    .document('conversations/{conversationId}/messages/{messageId}')
    .onCreate(async (snap, _) => {
        const senderId = snap.data()['sender_id'];
        const recipientId = snap.data()['recipient_id']; // topic for messaging
        const messageType = snap.data()['message_type'];

        if (senderId !== null && recipientId !== null && messageType !== null) {
            let body = `User ${senderId} has sent you `;
            if (messageType === "image") {
                body += `an encrypted image message.`;
            } else if (messageType === "text") {
                body += `an encrypted text message.`;
            } else {
                body += `an encrypted message.`;
            }

            var condition = `'${recipientId}' in topics`;
            var message = {
                notification: {
                    title: 'Secure and Smart Messenger',
                    body: body
                },
                condition: condition
            };

            admin.messaging().send(message)
                .then(response => {
                    return console.log('Successfully sent message: ', response);
                })
                .catch(error => {
                    return console.log('Error sending message: ', error);
                });
        }

        return;
    });
