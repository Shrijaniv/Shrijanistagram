package com.example.shrijanistagram.fragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.shrijanistagram.LoginActivity
import com.example.shrijanistagram.Post
import com.example.shrijanistagram.R
import com.parse.*
import java.io.File

class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    lateinit var ivPreview: ImageView
    lateinit var etDescription: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPreview = view.findViewById(R.id.Image)
        etDescription = view.findViewById(R.id.etDescription)
        view.findViewById<Button>(R.id.btTakePicture).setOnClickListener{
            onLaunchCamera()

        }

        view.findViewById<Button>(R.id.Send).setOnClickListener {
            val description = view.findViewById<EditText>(R.id.etDescription).text.toString()
            val User = ParseUser.getCurrentUser()
            if(photoFile != null){
                submitPost(description, User, photoFile!!)
            } else{
                Toast.makeText(requireContext(), "Post failed to send, add a picture", Toast.LENGTH_SHORT).show()
            }

        }

        view.findViewById<Button>(R.id.Logout).setOnClickListener{
            ParseUser.logOut()
            goToLoginActivity()

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun submitPost(description: String, User: ParseUser, Image: File){

        val post = Post()
        post.setDescription(description)
        post.setUser(User)
        post.setImage(ParseFile(Image))
        post.saveInBackground{ exception ->
            if (exception != null){
                Log.e(TAG, "Error while saving the post")
                exception.printStackTrace()
                Toast.makeText(requireContext(), "Error while saving post", Toast.LENGTH_SHORT).show()
            } else{
                Log.i(TAG, "Successfully saved the post")
                etDescription.setText("")
                ivPreview.setImageResource(0)
            }
        }

    }



    private fun goToLoginActivity(){
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)  //COME BACK TO THIS finish(). TODO
    }


    companion object{
        const val TAG = "MainActivity"
    }
}