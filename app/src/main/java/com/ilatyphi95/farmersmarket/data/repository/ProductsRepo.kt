package com.ilatyphi95.farmersmarket.data.repository

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ilatyphi95.farmersmarket.data.entities.Product
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
                    imgsUrl, 100, 50, "8"
                )
            }
        Log.d(TAG, product.toString())
        dbRef.setValue(product)
            .addOnSuccessListener {
                Log.d(TAG, "saved products to database")
                //clear photoList
            }
            .addOnFailureListener {
                Log.d(TAG, "faile to save products to db ${it.message}")
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

    companion object {
        val TAG = "ProductsRepo"
    }
}