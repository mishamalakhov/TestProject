package android.malakhov.testproject.data.location

import java.util.*
import kotlin.collections.ArrayList

class LocationType() {

    var id: String = UUID.randomUUID().toString()
    var name: String = "Без названия"
    var packageCount = -1
    var packagesList: ArrayList<PhotosPackage> = ArrayList<PhotosPackage>()

}