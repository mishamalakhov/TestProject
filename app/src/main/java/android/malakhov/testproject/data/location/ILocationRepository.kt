package android.malakhov.testproject.data.location

import android.graphics.Bitmap

interface ILocationRepository {

    fun setLocation(location: LocationType)
    fun loadPhotos(
        location: LocationType, pack: PhotosPackage, bitmap: Bitmap,
        id: String
    )

    fun deletePhotos(location: LocationType,pack: PhotosPackage, list: ArrayList<String>)

    fun deletePackage(location: LocationType, pack: PhotosPackage);
}