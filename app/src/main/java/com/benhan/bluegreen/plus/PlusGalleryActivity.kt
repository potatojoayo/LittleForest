package com.benhan.bluegreen.plus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.*
import com.benhan.bluegreen.adapter.GalleryAdapter
import com.benhan.bluegreen.dataclass.PhotoVO
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.place.PhotoUploadToPageActivity
import com.benhan.bluegreen.utill.GetImageUri
import com.benhan.bluegreen.utill.GridDividerDecoration
import com.bumptech.glide.Glide
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.theartofdev.edmodo.cropper.CropImage


class PlusGalleryActivity: AppCompatActivity() {

    var placeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plus_fragment_gallery)




        val recyclerView = findViewById<RecyclerView>(R.id.galleryRecyclerView)
        val selectedView = findViewById<ImageView>(R.id.selectedImage)

        placeId = intent.getIntExtra("place_id", 0)

        recyclerView.setHasFixedSize(true)


        val getImageUri = GetImageUri(this)
        val list = getImageUri.getImageUri()

        val galleryAdapter =
            GalleryAdapter(this, list)









        val slidingLayout = findViewById<SlidingUpPanelLayout>(R.id.sliding_layout)

        slidingLayout.coveredFadeColor = 0



        val ivX = findViewById<ImageView>(R.id.ivX)
        val ivNext = findViewById<TextView>(R.id.plusNext)

        ivX.setOnClickListener {

            finish()
        }




        galleryAdapter.list[0].selected
        Glide.with(this).load(galleryAdapter.list[0].imgPath)
            .centerCrop().into(selectedView)
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED







        val mOnItemClickListener = object :
            OnItemClickListener {


            override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {
                val photoVO: PhotoVO = galleryAdapter.list[position]
                val photoVOSelectedBefore: PhotoVO? = galleryAdapter.list.find {
                    it.selected
                }


                if (photoVO.selected){

                    if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.HIDDEN ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){

                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED

                    }


                    if(slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED){


                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                    }







                }else{


                    if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.HIDDEN ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED

                    }

                    photoVOSelectedBefore?.selected = false
                    photoVO.selected = true

                    Glide.with(this@PlusGalleryActivity).load(photoVO.imgPath)
                        .centerCrop()
                        .into(selectedView)

                }
                galleryAdapter.list[position] = photoVO
                galleryAdapter.notifyDataSetChanged()

                ivNext.setOnClickListener {



                    CropImage.activity(photoVO.imgPath)
                        .setInitialCropWindowPaddingRatio(0f)
                        .setMinCropWindowSize(300, 300)
                        .setMinCropResultSize(300, 300)
                        .start(this@PlusGalleryActivity)


                    Log.d("널?", galleryAdapter.list[position].imgPath.toString())


//



                }

            }


        }

        galleryAdapter.onItemClickListener = mOnItemClickListener
        val dividerDecoration = GridDividerDecoration(
            resources,
            R.drawable.divider_recyler_gallery
        )

        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = galleryAdapter
        recyclerView.addItemDecoration(dividerDecoration)
        recyclerView.itemAnimator = DefaultItemAnimator()




    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK){
                val resultUri = result.uri

                val intent = Intent(this@PlusGalleryActivity, PhotoUploadActivity::class.java)
                    intent.putExtra("photo", resultUri.toString())
                if(placeId != 0) {
                    val toPageUpload = Intent(this, PhotoUploadToPageActivity::class.java)
                    toPageUpload.putExtra("place_id", placeId)
                    toPageUpload.putExtra("photo", resultUri.toString())
                    startActivity(toPageUpload)
                }else {
                    startActivity(intent)
                }
            }

            finish()
        }
    }


}

