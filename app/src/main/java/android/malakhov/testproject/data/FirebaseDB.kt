package android.malakhov.testproject.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseDB {

    private var dataBase: FirebaseFirestore? = null     //Firestore object
    private val storageRef: StorageReference =
        FirebaseStorage.getInstance().getReference("Photos")    //Firebase storage reference
    private val storage = FirebaseStorage.getInstance() //Firebase storage

    init { dataBase = FirebaseFirestore.getInstance() }

    //Singletone
    companion object{
        var obj: FirebaseDB? = null

        fun get(): FirebaseDB? {
            if (obj == null)
                obj = FirebaseDB()
            return obj
        }
    }

    //Get FirebaseFirestore object
    fun getFirebaseFirestore(): FirebaseFirestore? {
        return dataBase
    }

    //Get StorageRef
    fun getStorageRef(): StorageReference? {
        return storageRef
    }

    //Get firebase storage
    fun getStorage(): FirebaseStorage{
        return storage
    }
}