package android.malakhov.testproject.data.location

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PhotosPackage {
    var name:String = ""
    var uriList = HashMap<String, String>()
    var id: String = UUID.randomUUID().toString()
    var index = -1  //Index of a package in Location ArrayList
}