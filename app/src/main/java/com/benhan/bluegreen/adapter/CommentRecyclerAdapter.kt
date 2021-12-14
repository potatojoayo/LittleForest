package com.benhan.bluegreen.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.user.OtherUser
import com.benhan.bluegreen.R
import com.benhan.bluegreen.dataclass.ServerResonse
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.dataclass.CommentData
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.Functions
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.pnikosis.materialishprogress.ProgressWheel
import de.hdodenhof.circleimageview.CircleImageView
import org.ocpsoft.prettytime.PrettyTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class CommentRecyclerAdapter(val context: Context, val commentList: ArrayList<CommentData>, val progressWheel : ProgressWheel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val functions = Functions(context)
    var onClickReply: OnClickReply? = null

    var onItemDeleted: OnItemDeleted? = null

    val sharedPreference = SharedPreference()

    companion object {
        const val TYPE_COMMENT = 0
        const val TYPE_REPLY = 1
    }

    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvName: TextView = view.findViewById(R.id.posterName)
        val tvContents: TextView = view.findViewById(R.id.descriptionContents)
        val tvDate: TextView = view.findViewById(R.id.postedDate)
        val tvLikes: TextView = view.findViewById(R.id.commentLikes)
        val tvReply: TextView = view.findViewById(R.id.reply)
        val ivUserProfile: CircleImageView = view.findViewById(R.id.profilePhoto)
        val description: RelativeLayout = view.findViewById(R.id.description)
        val btnLike: ImageView = view.findViewById(R.id.btnLike)
        val tvDelete: TextView = view.findViewById(R.id.delete_comment)


    }


    class ReplyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvName: TextView = view.findViewById(R.id.posterName)
        val tvContents: TextView = view.findViewById(R.id.descriptionContents)
        val tvDate: TextView = view.findViewById(R.id.postedDate)
        val tvLikes: TextView = view.findViewById(R.id.commentLikes)
        val tvReply: TextView = view.findViewById(R.id.reply)
        val ivUserProfile: CircleImageView = view.findViewById(R.id.profilePhoto)
        val description: RelativeLayout = view.findViewById(R.id.description)
        val btnLike: ImageView = view.findViewById(R.id.btnLike)
        val tvDelete: TextView = view.findViewById(R.id.delete_comment)

    }

    override fun getItemViewType(position: Int): Int {

        return if (commentList[position].is_reply!! == 0) {
            TYPE_COMMENT
        } else {
            TYPE_REPLY
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val commentLayout =
            inflater.inflate(R.layout.comment_recyclerview_item_comment, parent, false)
        val replyLayout = inflater.inflate(R.layout.comment_recyclerview_item_reply, parent, false)



        return if (viewType == TYPE_COMMENT) {
            CommentViewHolder(
                commentLayout
            )
        } else {
            ReplyViewHolder(
                replyLayout
            )
        }
    }


    override fun getItemCount(): Int {
        return commentList.size
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val item = commentList[position]


        val prettyTime = PrettyTime(Locale.KOREA)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val date = simpleDateFormat.parse(item?.date)
        val ago = prettyTime.format(date)
        val myEmail = sharedPreference.getString(context, "email")
        val myName = sharedPreference.getString(context, "name")


        //////////////// Comment View


        if (holder is CommentViewHolder) {

            var isLiked = item.is_liked
            var likes = item?.likes!!

            /*                 set data into view                       */
            holder.tvName.text = item?.user_name
            holder.tvContents.text = item?.contents
            holder.tvDate.text = ago
            if (likes == 0) {
                holder.tvLikes.visibility = View.GONE
            }
            holder.tvLikes.text = "좋아요 ${likes} 개"
            holder.tvReply.text = "답글달기"

            /*                view function                     */






            fun startOtherUserPage() {
                val intent = Intent(context, OtherUser::class.java)
                intent.putExtra("otherUserName", item?.user_name)
                context.startActivity(intent)
            }

            fun likeComment(email: String, commentId: Int, user_name: String) {

                val call: Call<ServerResonse> = apiInterface.likeComment(email, commentId, user_name)
                call.enqueue(object : Callback<ServerResonse> {
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {
                        Log.d("패일", t.message)
                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {

                    }
                })

            }

            fun unlikeComment(email: String, commentId: Int) {

                val call: Call<ServerResonse> = apiInterface.unLikeComment(email, commentId)
                call.enqueue(object : Callback<ServerResonse> {
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {

                    }
                })


            }


            fun replyClicked() {
                onClickReply?.onClickReply(item?.user_name!!, holder.layoutPosition)
            }


            /*                     set function                           */






            holder.tvReply.setOnClickListener {
                replyClicked()
            }


            val profilePhotoUri = MyApplication.severUrl+item.profile_photo
            Glide.with(context).load(profilePhotoUri)
                .override(holder.ivUserProfile.width, holder.ivUserProfile.height)
                .into(holder.ivUserProfile)

            if (isLiked!!){
                holder.btnLike.setImageResource(R.drawable.tree_selected)
            } else {
                holder.btnLike.setImageResource(R.drawable.tree)
            }


            holder.btnLike.setOnClickListener {
                if(isLiked!!) {
                    holder.btnLike.setImageResource(R.drawable.tree)
                    unlikeComment(myEmail!!, item?.comment_no!!)
                    isLiked = false
                    likes -= 1
                    if(likes == 0){
                        holder.tvLikes.visibility = View.GONE
                    }
                    holder.tvLikes.text = "좋아요 ${likes} 개"

                } else {
                    holder.btnLike.setImageResource(R.drawable.tree_selected)
                    likeComment(myEmail!!, item?.comment_no!!, myName!!)
                    isLiked = true
                    likes += 1
                    if(likes > 0){
                        holder.tvLikes.visibility = View.VISIBLE
                    }
                    holder.tvLikes.text = "좋아요 ${likes} 개"
                }

            }


            holder.ivUserProfile.setOnClickListener {
                startOtherUserPage()
            }
            holder.tvName.setOnClickListener {
                startOtherUserPage()
            }

            if(myName == item.user_name) {

            holder.tvDelete.visibility = View.VISIBLE
                holder.tvDelete.setOnClickListener {

                    progressWheel.visibility = View.VISIBLE
                    deleteComment(item.comment_no!!, position)


                }

            } else{

                holder.tvDelete.visibility = View.GONE
                holder.tvDelete.setOnClickListener {

                }

            }

        }


        ///////////////Reply View


        else if (holder is ReplyViewHolder) {

            var isLiked = item.is_liked
            var likes = item.likes!!
            /*                 set data into view                       */
            holder.tvName.text = item?.user_name
            holder.tvContents.text = item?.contents
            holder.tvDate.text = ago
            if (likes == 0) {
                holder.tvLikes.visibility = View.GONE
            }
            holder.tvLikes.text = "좋아요 ${likes} 개"

            holder.tvReply.text = "답글달기"

            /*                view function                     */






            fun startOtherUserPage(userName: String) {
                val intent = Intent(context, OtherUser::class.java)
                intent.putExtra("otherUserName", userName)
                context.startActivity(intent)
            }

            fun likeReply(email: String, replyId: Int, user_name: String) {

                val call: Call<ServerResonse> = apiInterface.likeReply(email, replyId, user_name)
                call.enqueue(object : Callback<ServerResonse> {
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

                        Log.d("패일", t.message)
                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {


                    }
                })

            }

            fun unlikeReply(email: String, replyId: Int) {

                val call: Call<ServerResonse> = apiInterface.unLikeReply(email, replyId)
                call.enqueue(object : Callback<ServerResonse> {
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {

                    }
                })


            }


            fun replyClicked() {
                onClickReply?.onClickReply(item?.user_name!!, holder.layoutPosition)
            }


            /*                     set function                           */






            holder.tvReply.setOnClickListener {
                replyClicked()
            }


            val profilePhotoUri = MyApplication.severUrl+item.profile_photo
            Glide.with(context).load(profilePhotoUri)
                .override(holder.ivUserProfile.width, holder.ivUserProfile.height)
                .into(holder.ivUserProfile)

            if (isLiked!!){
                holder.btnLike.setImageResource(R.drawable.tree_selected)
            } else {
                holder.btnLike.setImageResource(R.drawable.tree)
            }


            holder.btnLike.setOnClickListener {
                if(isLiked!!) {
                    holder.btnLike.setImageResource(R.drawable.tree)
                    unlikeReply(myEmail!!, item?.reply_id!!)
                    isLiked = false
                    likes -= 1
                    if(likes == 0){
                        holder.tvLikes.visibility = View.GONE
                    }
                    holder.tvLikes.text = "좋아요 ${likes} 개"
                } else {
                    holder.btnLike.setImageResource(R.drawable.tree_selected)
                    likeReply(myEmail!!, item?.reply_id!!, myName!!)
                    isLiked = true
                    likes += 1
                    if(likes > 0){
                        holder.tvLikes.visibility = View.VISIBLE
                    }
                    holder.tvLikes.text = "좋아요 ${likes} 개"
                }

            }



                holder.ivUserProfile.setOnClickListener {
                    startOtherUserPage(item.user_name!!)
                }
                holder.tvName.setOnClickListener {
                    startOtherUserPage(item.user_name!!)
                }

            if(myName == item.user_name) {

                holder.tvDelete.visibility = View.VISIBLE
                holder.tvDelete.setOnClickListener {

                    deleteReply(item.reply_id!!)

                    if(onItemDeleted != null)
                        onItemDeleted?.onItemDeleted(position)

                }
            } else {

                holder.tvDelete.visibility = View.GONE
                holder.tvDelete.setOnClickListener {

                }

            }

            }



        }

    interface OnClickReply {

        fun onClickReply(userName: String, position: Int)

    }

    fun addOnClickReply(listener: OnClickReply) {

        onClickReply = listener

    }

    fun deleteComment(comment_no: Int, position: Int){

        val call: Call<ServerResonse> = apiInterface.delteComment(comment_no, null)
        call.enqueue(object : Callback<ServerResonse>{
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {
                if(onItemDeleted != null)
                    onItemDeleted?.onItemDeleted(position)
                progressWheel.visibility = View.GONE

            }

            override fun onResponse(call: Call<ServerResonse>, response: Response<ServerResonse>) {



            }


        })


    }
    fun deleteReply(id: Int){

        val call: Call<ServerResonse> = apiInterface.delteComment(null, id)
        call.enqueue(object : Callback<ServerResonse>{
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

            }

            override fun onResponse(call: Call<ServerResonse>, response: Response<ServerResonse>) {

            }


        })


    }

    interface OnItemDeleted{


        fun onItemDeleted(position: Int)


    }



    }

