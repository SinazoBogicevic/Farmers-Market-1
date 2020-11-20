package com.ilatyphi95.farmersmarket.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.ilatyphi95.farmersmarket.data.entities.*
import java.util.*

class Repository : IRepository {
    private val messagesList = mutableListOf<ChatMessage>()

    fun Repository(): Repository {
        if(instance == null) {
            instance = Repository()
        }
        return instance
    }

    override fun searchProducts(searchString: String): LiveData<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(sellerId: String): User {
        var user: User? = null
        //val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$sellerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${user!!.firstName}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        return user!!
    }

    override suspend fun allProducts(): List<Product> {
        //all products from database
        val list = mutableListOf<Product>()

        val ref = FirebaseDatabase.getInstance().getReference("/products")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.children.forEach {
                    val product = it.getValue(Product::class.java)
                    if (product != null) {
                        //add product to list
                        list.add(product)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return list
    }

    override suspend fun getRecentProducts(): List<Product> {
        val list = mutableListOf<Product>()

        val ref = FirebaseDatabase.getInstance().getReference("/recentProducts")

        ref.limitToFirst(6).addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.children.forEach {
                    val product = it.getValue(Product::class.java);
                    if (product != null){}
                    list.add(product!!);
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return list
    }

    override suspend fun getCloseByProduct(): List<CloseByProduct> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategory(): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPicture(file: Uri?): String {
        var imgUrl: String? = null
        val TAG = "imgUpload"
        if (file == null) ""

        val uid = FirebaseAuth.getInstance().uid
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$uid/$filename")

        ref.putFile(file!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")
                    imgUrl = it.toString()
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
        return imgUrl!!
    }

    override fun getCurrentUser(): User {
        var currentUser: User? = null
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser!!.firstName}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        return currentUser!!
    }

    override suspend fun getUserProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/products").child("$uid")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
                val product = snapshot.getValue(Product::class.java)

                if(product != null) {
                    list.add(product)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return list
    }

    override fun insertProduct(product: Product) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val productId = FirebaseDatabase.getInstance().getReference("products/$uid").push().key
        val dbRef = FirebaseDatabase.getInstance().getReference("/products/$uid/$productId")

        dbRef.setValue(product)
            .addOnSuccessListener {
                Log.d("success", "saved products to database")
                //add product to recent products node
                insertRecentProduct(product)
            }
            .addOnFailureListener {
                Log.d("success", "failed to save products to db ${it.message}")
                //alert user to failure
            }
    }

    override fun getMessages(messageId: String): LiveData<MutableList<ChatMessage>> {
        val messages  = MutableLiveData<MutableList<ChatMessage>>()
        val list = mutableListOf<ChatMessage>()
        val senderId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$senderId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java) ?: return
                //latestMessagesMap[dataSnapshot.key!!] = chatMessage
                //refreshRecyclerViewMessages()
                list.add(chatMessage)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                //latestMessagesMap[p0.key!!] = chatMessage
                //refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        messages.setValue(list)
        return messages
    }

    override suspend fun getMessageRecipients(messageId: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun sendMessage(chatMessage: ChatMessage) {
        val senderId = FirebaseAuth.getInstance().uid
        val receiverId = chatMessage.receiverId

        if (senderId == null) return

        val senderRef = FirebaseDatabase.getInstance().getReference("/user-messages/$senderId/$receiverId").push()

        val receiverRef = FirebaseDatabase.getInstance().getReference("/user-messages/$receiverId/$senderId").push()

        senderRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("success", "Saved our chat message: ${senderRef.key}")
                //clear editText
                //scroll to latest message in chatFragment
            }

        receiverRef.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$senderId/$receiverId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageSenderRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$receiverId/$senderId")
        latestMessageSenderRef.setValue(chatMessage)
    }

    override suspend fun getPostedAds(): List<AddItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getInterestedAds(): List<AddItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getAd(itemId: String): Product {
        TODO("Not yet implemented")
    }

    override suspend fun getMessageList(): List<Message>? {
        TODO("Not yet implemented")
    }

    override fun insertRecentProduct(product: Product) {
        val ref = FirebaseDatabase.getInstance().getReference("/recentProducts")

        ref.setValue(product)
            .addOnSuccessListener {
                Log.d("success", "added to recentProducts")
            }
            .addOnFailureListener {
                it.stackTrace
            }
    }

    companion object {
        lateinit var instance : Repository

    }
}