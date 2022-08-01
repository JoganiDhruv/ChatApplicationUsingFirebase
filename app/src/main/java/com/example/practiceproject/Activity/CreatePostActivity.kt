package com.example.practiceproject.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.example.practiceproject.Daos.PostDao
import com.example.practiceproject.R

class CreatePostActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var addImageText: TextView
    private lateinit var image : ImageView

   private lateinit var postDao : PostDao

    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        postDao = PostDao()

        addImageText = findViewById(R.id.post_add_image_textview)
        button = findViewById(R.id.post_button)
        editText = findViewById(R.id.post_editText)
        image = findViewById(R.id.post_setImage)


        addImageText.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        button.setOnClickListener {
//            if (imageUri == null){
//                editText.setError("selectImage")
//            }else {
                val data = editText.text.toString().trim()
                if (data.isNotEmpty()) {
                    postDao.addPost(data,imageUri?.toString()?:"")
                    finish()

                }
//            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage)
        {
            imageUri = data?.data
            image.setImageURI(imageUri)
//            Log.d("ImageURI",""+imageUri)
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        this.finish()
    }
}