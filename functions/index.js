const functions = require('firebase-functions')
const admin = require('firebase-admin')

const firebase = admin.initializeApp();
const db = admin.firestore();

exports.deleteConversationImages = functions.firestore
    .document('conversations/{conversationId}')
    .onDelete(async (snap, context) => {
        return db.collection("conversations")
            .doc(context.params.conversationId)
            .collection('messages')
            .get()
            .then(snapshot => {
                const bucket = firebase.storage().bucket();
                let errorPaths = []
                snapshot.filter(msg => {
                    return msg.data().message_type === "image";
                }).map(imgMsg => {
                    return imgMsg.data().bucket_path;
                }).forEach(bucketImgPath => {
                    bucket.child(bucketImgPath).then(() => {
                        console.log("File: " + bucketImgPath + " deleted successfully");
                        return;
                    }).catch(err => {
                        console.log("Error deleting: " + bucketImgPath + " " + err);
                        errorPaths.push(bucketImgPath);
                        return;
                    });
                });
                return errorPaths;
            });
    });
