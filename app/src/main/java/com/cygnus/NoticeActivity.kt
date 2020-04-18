package com.cygnus

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.NoticeBoardDao
import com.cygnus.model.NoticeBoardPost
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.MessageInputDialog
import com.cygnus.view.NoticeBoardPostView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_list.*

class NoticeActivity : DashboardChildActivity() {

    private lateinit var posts: ArrayList<NoticeBoardPost>
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val posts = intent.getParcelableArrayListExtra<NoticeBoardPost>(CygnusApp.EXTRA_NOTICE_POSTS)
        if (posts == null) {
            finish()
            return
        }

        this.posts = posts
        this.adapter = PostAdapter(this, posts)
        this.adapter.sort { o1, o2 ->
            (o2 as NoticeBoardPost).postDate.compareTo((o1 as NoticeBoardPost).postDate)
        }

        addButton.visibility = when (currentUser) {
            is Teacher -> {
                addButton.setOnClickListener { onAddNoticeClicked() }
                View.VISIBLE
            }
            else -> View.GONE
        }
    }

    override fun updateUI(currentUser: User) {
        contentList.adapter = this.adapter
    }

    private fun onAddNoticeClicked() {
        MessageInputDialog(this)
                .setOnMessageReceivedListener { message ->
                    try {
                        val status = Snackbar.make(contentList, "Sending...", Snackbar.LENGTH_INDEFINITE)
                        status.show()

                        val post = NoticeBoardPost(postContent = message, postAuthor = currentUser.name)
                        NoticeBoardDao.add(schoolId, (currentUser as Teacher).classId!!, post, OnCompleteListener {
                            if (it.isSuccessful) {
                                status.setText("Message sent!")
                                posts.add(post)
                                adapter.notifyDataSetChanged()
                                adapter.sort { o1, o2 ->
                                    (o2 as NoticeBoardPost).postDate.compareTo((o1 as NoticeBoardPost).postDate)
                                }
                            } else {
                                status.setText(it.exception?.message ?: "Could not send the message at this time.")
                            }

                            Handler().postDelayed({
                                status.dismiss()
                            }, 1500L)
                        })
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                .show()
    }

    private inner class PostAdapter(context: Context, posts: ArrayList<NoticeBoardPost>)
        : ModelViewAdapter<NoticeBoardPost>(context, posts, NoticeBoardPostView::class)

}