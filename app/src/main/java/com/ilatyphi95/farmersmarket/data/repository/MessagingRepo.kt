package com.ilatyphi95.farmersmarket.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.ilatyphi95.farmersmarket.data.entities.ChatMessage
import com.ilatyphi95.farmersmarket.data.entities.User

class MessagingRepo {

    /**
     * @param receiver refers to whom the message is being sent
     */

    private fun listenForMessages(receiver: User) {
        val chatId = FirebaseAuth.getInstance().uid
        val toId = receiver?.id
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$chatId/$toId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.msg)

                    //check who sent the message
                    if (chatMessage.chatId == FirebaseAuth.getInstance().uid) {
                        //add to adapter that shows chats
                    } else {
                        //add to adapter that shows chats
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    /**
     * sends messages to database
     * @param receiver user who is receiving the message
     */

    private fun performSendMessage(receiver: User) {
        // how do we actually send a message to firebase...
        val message = "hello"

        val chatId = FirebaseAuth.getInstance().uid
        val receiverId = receiver.id

        if (chatId == null) return

//    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$chatId/$receiverId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$receiverId/$chatId").push()

        val chatMessage = ChatMessage(chatId, "mediaThumbUrl", "mediaUrl", message, receiverId, "", "", System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                //clear editText
                //recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                //scroll to latest message in chatFragment
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$chatId/$receiverId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$receiverId/$chatId")
        latestMessageToRef.setValue(chatMessage)
    }

    companion object {
        val TAG = "msgRepo"
    }
}