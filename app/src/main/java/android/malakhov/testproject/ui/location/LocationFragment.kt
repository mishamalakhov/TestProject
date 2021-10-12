package android.malakhov.testproject.ui.location

import android.app.Activity
import android.content.Intent
import android.malakhov.testproject.R
import android.malakhov.testproject.data.location.LocationType
import android.malakhov.testproject.data.location.PhotosPackage
import android.malakhov.testproject.databinding.FragmentLocationBinding
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


class LocationFragment : Fragment() {

    private var bigPlus: ImageView? = null  //button for adding package of photos
    private lateinit var viewModel: LocationViewModel // ViewModel
    private var recycler: RecyclerView? = null  //recycler
    private var adapter: MyAdapter? = null  //recycler adapter
    private lateinit var location: LocationType  //Location object


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //get Binding
        val binding: FragmentLocationBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false)

        //Initialize variables
        init(binding)

        //Prepare the recycler
        recycler?.layoutManager = LinearLayoutManager(activity)
        recycler?.adapter = adapter

        //When we click a recycler the delete button disappear
        recycler?.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    adapter?.setDeleteBtnVisibility()
                    adapter?.notifyDataSetChanged()
                }

            }
            false
        }

        //Add package when bigPlus is clicked and load Location to DB
        bigPlus?.setOnClickListener(View.OnClickListener {
            location.packageCount++
            val photoPackage = PhotosPackage()
            photoPackage.index = location.packageCount
            location.packagesList.add(photoPackage)
            adapter?.notifyDataSetChanged()
            loadLocationToDB()
        })

        return binding.root
    }


    //Creating fragment
    companion object {
        fun newInstance() = LocationFragment()
    }

    //All variables are initialized here
    private fun init(binding: FragmentLocationBinding) {
        bigPlus = binding.bigPlus
        recycler = binding.recyclerView
        location = LocationType()
        adapter = MyAdapter(location.packagesList, this)
    }

    //Creating the viewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        //Load location to DB when user open Location fragment screen
        loadLocationToDB()
    }

    //Open a gallery and get Photos
    fun openGallery(index: Int) {
        var intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, index)
    }

    //Close a gallery and getResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) return

        val pack = location.packagesList[requestCode]
        val id = UUID.randomUUID().toString()
        pack.uriList[id] = data?.data.toString()
        adapter?.notifyDataSetChanged()
        loadLocationToDB()
        loadPhotosToDB(data?.data.toString(), pack, id)
    }

    //Load photos into DB
    fun loadPhotosToDB(uri: String, pack: PhotosPackage, id: String) {
        val bitmap = MediaStore.Images.Media.getBitmap(
            activity?.contentResolver,
            Uri.parse(uri)
        )
        viewModel.loadPhotosToDB(location, pack, bitmap, id)
    }

    //Load location into DB
    fun loadLocationToDB() {
        viewModel.setLocation(location)
    }

    //Delete photos from DB
    fun deletePhotosFromDB(pack: PhotosPackage, list: ArrayList<String>){
        viewModel.deletePhotosFromDB(location, pack, list)
    }

    fun deletePackageFromDB(pack: PhotosPackage){
        viewModel.deletePackageFromDB(location,pack)
    }



    //ViewHolder class*******************************************************************

    class MyViewHolder(itemView: View, var inflater: LayoutInflater, var type: Int,
                       var frag: LocationFragment) :
        RecyclerView.ViewHolder(itemView) {

        var btn: ImageView? = null  //plus-button fot adding photos
        var grid: GridLayout? = null    //package-Grid
        var locationName: EditText? = null  // Edit text with Location name
        var packageName: EditText? = null  // Edit text with photos-package name
        var deleteBtn: TextView? = null  // Edit text with photos-package name
        var swipeDelete: CardView? = null   //When we swipe this button appears for delete
        var scroll: HorizontalScrollView? = null  //ScrollView

        init {
            //If the element isn't header so we initialize its views
            if (type != 0) {
                btn = itemView.findViewById(R.id.smallPlus)
                grid = itemView.findViewById(R.id.grid)
                packageName = itemView.findViewById(R.id.packageName)
                deleteBtn = itemView.findViewById(R.id.deleteBtn)
                swipeDelete = itemView.findViewById(R.id.swipeDelete)
                scroll = itemView.findViewById(R.id.scroll)

                //Set width of view equals to Display width
                val display: Display? = frag.activity?.windowManager?.getDefaultDisplay()
                val iv = itemView.findViewById<CardView>(R.id.cardView)
                val width = display?.width // ((display.getWidth()*20)/100)
                val params = iv.layoutParams
                params.width = width!!
                iv.layoutParams = params
            }
            //the element is header and we initialize his views
            else locationName = itemView.findViewById(R.id.locationName)
        }

        fun onBind(pack: PhotosPackage?) {
            var i = 0   //number of image

            //Long click on package and then delete button appear
            grid?.setOnLongClickListener {
                deleteBtn?.visibility = View.VISIBLE
                onBind(pack)
                true
            }

            //If element isn't header so we bind photos
            if (type != 0) {
                grid?.removeAllViews()

                //bind all images from package
                for (photo in pack?.uriList!!) {
                    val view = inflater.inflate(R.layout.photo_layout, null, false)//layout of one photo
                    val imageView: ImageView = view.findViewById(R.id.photo)//imageView int photo layout

                    //here a checkbox = circle(ImageView) + cross(ImageView)
                    val uncheckedBox = view.findViewById<ImageView>(R.id.empty_check)//circle of checkbox for one image
                    val cross = view.findViewById<ImageView>(R.id.cross)//cross in check box for one image

                    //if deleteBtn is visible so circle checkboxes become visible too
                    if (deleteBtn?.visibility == View.VISIBLE) {
                        uncheckedBox.visibility = View.VISIBLE

                        //when we click on image then its check box become checked or unchecked
                        //(cross appear or disappear)
                        imageView.setOnClickListener{
                            if (cross.visibility == View.INVISIBLE) cross.visibility = View.VISIBLE
                            else cross.visibility = View.INVISIBLE
                        }
                    }
                    else cross.visibility = View.INVISIBLE

                    //Load photo into ImageView
                    Picasso.get().load(photo.value).fit().centerInside().into(imageView)

                    //Save id of image(id = key of uriList-hashMap) in image as a content description, so
                    // then I can delete this uri from pack and from DB
                    imageView.contentDescription = photo.key
                    //Add image to package(package = GridLayout)
                    grid?.addView(view)
                    i++
                }

                //deleteBtn onClick - delete uri from uriList-hashmap in package and then call deletePhotosFromDB-func
                //to delete photos from DB
                deleteBtn?.setOnClickListener{
                    val images = grid?.children?.toMutableList()
                    val list = ArrayList<String>()

                    images?.forEach {
                        val cross = it.findViewById<ImageView>(R.id.cross)
                        val image = it.findViewById<ImageView>(R.id.photo)

                        if (cross.visibility == View.VISIBLE){
                            grid!!.removeView(it)
                            pack.uriList.remove(image.contentDescription)
                            frag.location.packagesList[pack.index] = pack
                            list.add(image.contentDescription.toString())
                        }
                    }
                    frag.deletePhotosFromDB(pack, list)
                }
            }
        }
    }


    //Adapter class********************************************************

    class MyAdapter(list: ArrayList<PhotosPackage>?, var frag: LocationFragment) :
        RecyclerView.Adapter<MyViewHolder>() {

        private var list: ArrayList<PhotosPackage>? = ArrayList() //List of photo-packages
        private var deleteBtnVisibility = false     //Visibility of delete button

        init {
            this.list = list
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            //Set header if viewType == 0
            if (viewType == 0) {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_header, parent, false)
                return MyViewHolder(v, LayoutInflater.from(parent.context), viewType, frag)
            }
            //Set photoPackage View
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_element, parent, false)

            return MyViewHolder(v, LayoutInflater.from(parent.context), viewType, frag  )

        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.swipeDelete?.setOnClickListener{
                val pack = frag.location.packagesList[position-1]
                holder.scroll?.scrollTo(holder.scroll!!.left, 0)
                frag.location.packagesList.remove(list?.get(position-1))
                frag.location.packageCount--
                frag.adapter?.notifyDataSetChanged()
                frag.deletePackageFromDB(pack)
            }

            if (!deleteBtnVisibility)
                holder.deleteBtn?.visibility = View.GONE

            //If position isn't 0(element isn't a header) so we call onBind
            //Position = position-1 because there is a header with index 0
            if (position != 0) {
                holder.onBind(list?.get(position - 1))
                holder.btn?.setOnClickListener(View.OnClickListener {
                    frag.openGallery(position - 1)
                })

                //Load location to DB after write a name of any photos-package
                holder.packageName?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        frag.location.packagesList[position - 1].name = s.toString()
                        frag.loadLocationToDB()
                    }
                })
            }
            //The element is header and we load Location to DB after we wrote his name
            else {
                holder.locationName?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        frag.location.name = s.toString()
                        frag.loadLocationToDB()
                    }
                })
            }
        }

        //ItemCount = itemCount + 1 because there is a header with index 0
        override fun getItemCount(): Int {
            return list?.size!! + 1
        }

        //Check for a header
        override fun getItemViewType(position: Int): Int {
            return position
        }

        //Set delete button visibility = invisible when we touch a recycler
        fun setDeleteBtnVisibility():Boolean{
            return false
        }
    }
}
