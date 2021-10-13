package android.malakhov.testproject.ui.location

import android.R.attr
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.malakhov.testproject.R
import android.malakhov.testproject.data.location.LocationType
import android.malakhov.testproject.data.location.PhotosPackage
import android.malakhov.testproject.databinding.FragmentLocationBinding
import android.malakhov.testproject.ui.location.RecyclerAdapter.MyAdapter
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class LocationFragment : Fragment() {

    private var bigPlus: ImageView? = null  //button for adding package of photos
    private lateinit var viewModel: LocationViewModel // ViewModel
    private var recycler: RecyclerView? = null  //recycler
    var adapter: MyAdapter? = null  //recycler adapter
    lateinit var location: LocationType  //Location object
    val list = ArrayList<String>()   //list of packages id


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
            val pack = PhotosPackage()
            list.add(pack.id)
            location.packagesList[pack.id] = pack
            adapter?.notifyDataSetChanged()
            loadLocationToDB()
            recycler?.smoothScrollToPosition(adapter?.getItemCount()!!);
        })

        return binding.root
    }




    //All variables are initialized here
    private fun init(binding: FragmentLocationBinding) {
        bigPlus = binding.bigPlus
        recycler = binding.recyclerView
        location = LocationType()

        adapter = MyAdapter(list, this)
    }

    //Creating the viewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        //Load location to DB when user open Location fragment screen
        loadLocationToDB()
    }

    //Open a gallery and get Photos. id is the Id of a package
    var id = ""
    fun openGallery(id: String?) {
        this.id = id!!
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1)
    }

    //Close a gallery and get images uri. Then load images to DB
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) return
        val pack = location.packagesList[id]


            if (data?.clipData != null) {
                val count: Int = data.clipData!!.getItemCount()

                for (i in 0 until count) {

                    val uri: Uri = data.clipData!!.getItemAt(i).getUri()

                    val id = UUID.randomUUID().toString()
                    pack?.uriList?.set(id, uri.toString())
                    adapter?.notifyDataSetChanged()
                    loadLocationToDB()
                    loadPhotosToDB(uri.toString(), pack, id)
                }
            }
            else{
                val id = UUID.randomUUID().toString()
                pack?.uriList?.set(id, data?.data.toString())
                adapter?.notifyDataSetChanged()
                loadLocationToDB()
                loadPhotosToDB(data?.data.toString(), pack, id)
            }
    }

    //Load photos into DB
    fun loadPhotosToDB(uri: String, pack: PhotosPackage?, id: String) {
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

    //Delete package from DB
    fun deletePackageFromDB(pack: PhotosPackage){
        viewModel.deletePackageFromDB(location, pack)
    }
}
