package android.malakhov.testproject.ui.location

import android.graphics.Bitmap
import android.malakhov.testproject.data.location.LocationRepository
import android.malakhov.testproject.data.location.LocationType
import android.malakhov.testproject.data.location.PhotosPackage
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private var location: LocationType? = null

    fun setLocation(location: LocationType){
        LocationRepository.get()?.setLocation(location)
    }

    fun loadPhotosToDB(location: LocationType, pack: PhotosPackage?, bitmap: Bitmap, id:String){
            LocationRepository.get()?.loadPhotos(location, pack, bitmap, id)
    }

    fun deletePhotosFromDB(location: LocationType,pack: PhotosPackage, list: ArrayList<String>){
        LocationRepository.get()?.deletePhotos(location, pack, list)
    }

    fun deletePackageFromDB(location: LocationType, pack: PhotosPackage){
        LocationRepository.get()?.deletePackage(location, pack)
    }
}