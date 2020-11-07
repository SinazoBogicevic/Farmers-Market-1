package com.ilatyphi95.farmersmarket.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.ilatyphi95.farmersmarket.data.entities.ChatMessage
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User
import java.util.*

class ProductsRepo {
    var userProducts = mutableListOf<Product>()

    /**
     *sends the product to the database
     *
    private suspend fun makeRequest() {
        val photos = sendImagesToStorage(photoList)
        while (photos.size != photoList.size){
            Thread.sleep(1000L)
        }
        saveProductsToDb(photos)
    }
     *
     */


    /**
     * map images
     */
    private suspend fun sendImagesToStorage(images: MutableList<Uri>): MutableMap<String, String> {
        val map: MutableMap<String, String> = mutableMapOf()
        val uid = FirebaseAuth.getInstance().uid

        images.forEach { photoUri ->
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$uid/$filename")
            ref.putFile(photoUri)
                .addOnSuccessListener {
                    Log.d(TAG, "successfully uploaded img ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "file location $it")
                        map.put(filename, it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "failed to upload image ${it.message}")
                }
        }
        return map
    }

    /**
     * adds product to database
     */
    private suspend fun saveProductsToDb(imgsUrl: MutableMap<String, String>) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val productId = FirebaseDatabase.getInstance().getReference("products/$uid").push().key
        val dbRef = FirebaseDatabase.getInstance().getReference("/products/$uid/$productId")
        Log.d(TAG, imgsUrl.toString())
        val product =
            productId?.let {
                Product(
                    it, "product_name", "this is the product description", uid, "livestock",
                    imgsUrl, 100, 50, "8", 1024345978
                )
            }
        Log.d(TAG, product.toString())
        dbRef.setValue(product)
            .addOnSuccessListener {
                Log.d(TAG, "saved products to database")
                //clear photoList
            }
            .addOnFailureListener {
                Log.d(TAG, "failed to save products to db ${it.message}")
                //alert user to failure
            }
    }

    /**
     * retrieve all products
     */
    private suspend fun getAllProducts() {
        //all products from database

        val ref = FirebaseDatabase.getInstance().getReference("/products")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.children.forEach {
                    val product = it.getValue(Product::class.java)
                    if (product != null) {
                        println(product)

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

    }

    /**
     * gets all products posted by the logged in user
     */
    private suspend fun getUserProducts(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/products").child("$uid")

        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //println(snapshot.toString())
                val product = snapshot.getValue(Product::class.java)

                if(product != null){
                    userProducts.add(product)
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
        println(userProducts.size)
    }

    /**
    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }
    */

    /**
     * listens for latest messages
     * @param latestMessagesMap maps latest messages user has sent or received
     */

    private fun listenForLatestMessages(latestMessagesMap: MutableMap<String, ChatMessage>) {
        val chatId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$chatId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                //refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                //refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    /**
     * fetches logged in user info
     */

    private fun fetchCurrentUser(): User {
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

    companion object {
        val TAG = "ProductsRepo"
    }
}