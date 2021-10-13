package android.malakhov.testproject.data.location

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class PhotosPackage {
    var name:String = ""
    var uriList = LinkedHashMap<String, String>()
    var id: String = UUID.randomUUID().toString()
}