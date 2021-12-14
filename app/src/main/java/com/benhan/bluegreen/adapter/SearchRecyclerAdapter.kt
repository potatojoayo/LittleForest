package com.benhan.bluegreen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.R
import com.benhan.bluegreen.dataclass.PlaceSearchData
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.utill.AsyncFrameLayout
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class SearchRecyclerAdapter(val placeList: ArrayList<PlaceSearchData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object{
        const val TYPE_PLACE = 0
        const val TYPE_LOAD = 1

    }

    var isLoading = false
    var isMoreDataAvailable = true
    var onLoadMoreListener: HomeRecyclerAdapter.OnLoadMoreListener? = null
    var onItemClickListener: OnItemClickListener? = null

    inner class SearchHolder(val view: View, val context: Context): RecyclerView.ViewHolder(view){

        val ivPlacePhoto = view.findViewById<ImageView>(R.id.photo)
        val tvPlaceName = view.findViewById<TextView>(R.id.name)
        val tvPlaceProvince = view.findViewById<TextView>(R.id.province)
        val tvPlaceType = view.findViewById<TextView>(R.id.type)
        val layoutSelected = view.findViewById<RelativeLayout>(R.id.layoutSelect)
        val layoutItem = view.findViewById<RelativeLayout>(R.id.searchItem)
        val tvDistance = view.findViewById<TextView>(R.id.distance)


        fun bind(place: PlaceSearchData){


            if(place.photo != null && place.photo!!.isNotEmpty() && place.photo!!.isNotBlank()) {
                var photoUri = MyApplication.severUrl + place.photo

                Glide.with(context).load(photoUri)
                    .override(ivPlacePhoto.width, ivPlacePhoto.height)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivPlacePhoto)
            } else {
                ivPlacePhoto.setImageResource(
                    R.drawable.tree
                )
            }

            tvPlaceName.setText(place.name)
            tvPlaceProvince.setText(place.province)
            tvPlaceType.setText(place.type)

            if(place.distance == null){
                tvDistance.visibility = View.GONE
            }else {
                tvDistance.text = place.distance!!.toInt().toString()+"km"
                tvDistance.visibility = View.VISIBLE
            }
            if (place.isSelected){
                layoutSelected.visibility = View.VISIBLE
            }else {
                layoutSelected.visibility = View.INVISIBLE
            }



        }

    }

    class LoadHolder(view: View): RecyclerView.ViewHolder(view)

    override fun getItemId(position: Int): Long {
        return placeList[position].id.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {

        val context = parent.context
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemLayout = inflater.inflate(R.layout.search_recycler_item, parent, false)
        val loadLayout = inflater.inflate(R.layout.search_recycler_load, parent, false)

        if(viewType == TYPE_PLACE) {
            return SearchHolder(itemLayout, context)
        }else {
            return LoadHolder(loadLayout)
        }



    }


    override fun getItemCount(): Int {

        return placeList.size


    }

    override fun getItemViewType(position: Int): Int {

        if(placeList[position].kind == "place"){
            return TYPE_PLACE
        }else{
            return TYPE_LOAD
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(position >= itemCount-1 && isMoreDataAvailable && !isLoading && onLoadMoreListener!=null)

        {
            isLoading = true
            onLoadMoreListener!!.onLoadMore()
        }

        if(getItemViewType(position)== TYPE_PLACE) {
            val item = placeList[position]
            (holder as SearchHolder).bind(item)
            holder.layoutItem.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    if(onItemClickListener != null){
                        onItemClickListener?.OnItemClick(holder, position)
                    }
                }
            })


        }




    }


    fun notifyDataChanged(){
        notifyDataSetChanged()
        isLoading = false
    }


}