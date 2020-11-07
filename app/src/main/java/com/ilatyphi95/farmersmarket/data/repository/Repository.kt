package com.ilatyphi95.farmersmarket.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.ilatyphi95.farmersmarket.data.entities.*
import java.util.*

class Repository : IRepository {
    override fun searchProducts(searchString: String): LiveData<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(sellerId: String): User {
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

    override suspend fun getRecentProducts(): List<Product> {
        //all products from database
        val list = mutableListOf<Product>()

        val ref = FirebaseDatabase.getInstance().getReference("/products")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.children.forEach {
                    val product = it.getValue(Product::class.java)
                    if (product != null) {
                        //add product to list
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
        TODO("Not yet implemented")
    }

    override fun insertProduct(product: Product) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val productId = FirebaseDatabase.getInstance().getReference("products/$uid").push().key
        val dbRef = FirebaseDatabase.getInstance().getReference("/products/$uid/$productId")

        dbRef.setValue(product)
            .addOnSuccessListener {
                Log.d(ProductsRepo.TAG, "saved products to database")
                //do something
            }
            .addOnFailureListener {
                Log.d(ProductsRepo.TAG, "failed to save products to db ${it.message}")
                //alert user to failure
            }
    }

    override fun getMessages(messageId: String): LiveData<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMessageRecipients(messageId: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun sendMessage(chatMessage: ChatMessage) {
        TODO("Not yet implemented")
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
}