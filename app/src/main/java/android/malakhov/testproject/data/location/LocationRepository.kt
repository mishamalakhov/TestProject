package android.malakhov.testproject.data.location

import android.graphics.Bitmap
import android.malakhov.testproject.data.FirebaseDB
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream


class LocationRepository : ILocationRepository {

    private val db = FirebaseDB.get()?.getFirebaseFirestore()
    private val storageRef = FirebaseDB.get()?.getStorageRef()
    private val collection = db?.collection("Locations")

    //Singletone
    companion object {
        var obj: LocationRepository? = null

        fun get(): LocationRepository? {
            if (obj == null)
                obj = LocationRepository()
            return obj
        }
    }

    override fun setLocation(location: LocationType) {
        collection?.document(location.id)?.set(location)
    }

    //Saves photos in Storage  by way(location name/package name/ photo id(id = key from uriList-hashMap))
    //and then update a uri in package in firestore
     override fun loadPhotos(
        location: LocationType, pack: PhotosPackage?, bitmap: Bitmap,
        id: String
    ) {

            val locationRef = storageRef?.child(location.id)?.child(pack?.id!!)
                ?.child(id)

            var uploadURI: Uri?
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArray: ByteArray = baos.toByteArray()
            val up: UploadTask? = locationRef?.putBytes(byteArray)


            val task = up!!.continueWithTask<Uri> { locationRef.downloadUrl }
                .addOnCompleteListener { task ->
                    uploadURI = task.result
                    pack?.uriList!![id] = uploadURI.toString()
                    location.packagesList[pack.id] = pack
                    collection?.document(location.id)?.set(location)
                }
    }

    //Delete photos from Storage and FireStore
    override fun deletePhotos(
        location: LocationType,
        pack: PhotosPackage,
        list: ArrayList<String>
    ) {
        //delete photos from FireStore
        val ref = collection?.document(location.id)
        list.forEach {
            pack.uriList.remove(it)

            //Delete photos from Storage
            storageRef?.child(location.id+"/"+pack.id+"/"+it)?.downloadUrl
                ?.addOnSuccessListener { it1 ->
                    // Got the download URL for
                    val url = it1.toString()
                    FirebaseDB.get()?.getStorage()?.getReferenceFromUrl(url)?.delete()
                }
        }
        location.packagesList[pack.id] = pack
        ref?.update("packagesList", location.packagesList)

    }

    override fun deletePackage(location: LocationType, pack: PhotosPackage) {
        //delete package from firestor
        val ref = collection?.document(location.id)
        ref?.update("packagesList", location.packagesList)

        //delete package with photos from storage
        pack.uriList.forEach{
            storageRef?.child(location.id+"/"+pack.id+"/"+it.key)?.downloadUrl
                ?.addOnSuccessListener { it1 ->
                    // Got the download URL for
                    val url = it1.toString()
                    FirebaseDB.get()?.getStorage()?.getReferenceFromUrl(url)?.delete()
                }
        }
    }

}