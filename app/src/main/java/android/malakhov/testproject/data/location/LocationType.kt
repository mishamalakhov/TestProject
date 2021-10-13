package android.malakhov.testproject.data.location

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LocationType() {

    var id: String = UUID.randomUUID().toString()
    var name: String = "Без названия"
    var packagesList = HashMap<String, PhotosPackage>()

}