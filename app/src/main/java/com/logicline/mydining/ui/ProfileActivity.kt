package com.logicline.mydining.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityProfileBinding
import com.logicline.mydining.databinding.DialogEditProfileLayoutBinding
import com.logicline.mydining.databinding.DialogPasswordChangeBinding
import com.logicline.mydining.models.User
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.*
import com.logicline.mydining.utils.MyExtensions.longToast
import com.logicline.mydining.utils.MyExtensions.shortToast
import id.zelory.compressor.Compressor
import io.paperdb.Paper
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Locale


class ProfileActivity : AppCompatActivity() {
    lateinit var managerChangerListener: CompoundButton.OnCheckedChangeListener
    lateinit var binding : ActivityProfileBinding
    lateinit var loadingDialog: LoadingDialog

    var userProfile : User? = null
    var loggedUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userProfile = intent.getParcelableExtra<User>("profile")
        loggedUser = LocalDB.getUser()

        loadingDialog = LoadingDialog(this)

        if(Constant.isManagerOrSuperUser()){
            binding.cardManager.visibility = View.VISIBLE
            binding.switchManager.isEnabled = true
            binding.btnEditProfile.visibility  = View.VISIBLE

            binding.btnEditProfile.setOnClickListener {
                editProfile(userProfile)
            }
        }else{
            binding.cardManager.visibility = View.VISIBLE
            binding.switchManager.isEnabled = false
        }


        if(userProfile!=null){

            if(userProfile!!.id==loggedUser!!.id){
                //own profile

                //show edit profile
                binding.btnEditProfile.visibility  = View.VISIBLE

                //init password change dialog
                binding.changePassword.setOnClickListener {
                    showChangePasswordDialog()
                }

                binding.btnEditProfile.setOnClickListener {
                    editProfile(userProfile)
                }

            }else{
                //other profile
                //hide password change and image change view
                binding.cardPassChange.visibility = View.GONE
                binding.changeImg.visibility = View.GONE
                binding.cardLogout.visibility = View.GONE
            }




            setData(userProfile)


        }else{
            //own account
            setData(loggedUser)
        }

        managerChangerListener = CompoundButton.OnCheckedChangeListener {
                _, value -> changeManager(value)
        }

        addSwitchLister(true)

        binding.changeImg.setOnClickListener {
            pickImg()
        }

        binding.cardLogout.setOnClickListener {
            MyApplication.logOut(this)

        }

        binding.imgBack.setOnClickListener {
            finish()
        }




    }

    private fun editProfile(userProfile: User?) {

        var gender : String? = null

        val editBinding = DialogEditProfileLayoutBinding.inflate(layoutInflater)


        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(editBinding.root)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        editBinding.ccp.registerCarrierNumberEditText(editBinding.edtPhone)
        editBinding.ccp.setCountryForNameCode(userProfile?.country)
        editBinding.ccp.fullNumber = userProfile?.phone


        editBinding.edtEmail.setText(userProfile?.email)
        editBinding.edtCity.setText(userProfile?.city)
        editBinding.edtName.setText(userProfile?.name)


        val adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.layout_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            editBinding.spinnerGender.adapter = adapter
        }

        editBinding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position>0){
                    gender = parent?.getItemAtPosition(position).toString()
                }else{
                    gender= null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        for (i in 0 until adapter.count-1) {

            if(userProfile?.gender == adapter.getItem(i).toString()){
                editBinding.spinnerGender.setSelection(i, true)
                break
            }
        }

        editBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        editBinding.btnUpdate.setOnClickListener {
            val name = editBinding.edtName.text.toString()
            val email = editBinding.edtEmail.text.toString()
            val city = editBinding.edtCity.text.toString()
            val country = editBinding.ccp.selectedCountryNameCode

            if(country==null){
                shortToast("Please select country first.")
                return@setOnClickListener
            }


            if(!editBinding.ccp.isValidFullNumber){
                shortToast("Please enter valid mobile number")
                return@setOnClickListener
            }

            val phone = editBinding.ccp.fullNumber


            if(gender==null){
                shortToast("Please select your gender")
                return@setOnClickListener
            }

            if(name.isEmpty() || city.isEmpty() || name.isEmpty() || email.isEmpty()){
                Toast.makeText(this, "Every field must be not empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!Constant.isValidEmail(email)){
                shortToast("Email Not valid")
                return@setOnClickListener
            }


            loadingDialog.show()
            (application as MyApplication)
                .myApi
                .updateProfile(userProfile?.id!!.toInt(), name, country, city, phone, email, gender!!)
                .enqueue(object : Callback<ServerResponse<User>> {
                    override fun onResponse(
                        call: Call<ServerResponse<User>>,
                        response: Response<ServerResponse<User>>
                    ) {
                        loadingDialog.hide()
                       if(response.isSuccessful && response.body()!=null){
                           if(!response.body()!!.error){
                               response.body()!!.data?.let {
                                   if(LocalDB.getUserId()!! == userProfile!!.id?.toInt()){
                                       LocalDB.saveUser(it)
                                   }
                                   dialog.dismiss()
                                   shortToast("Profile Updated")
                                   setData(it)
                               }
                           }else{
                               shortToast(response.body()!!.msg)
                           }
                       }
                    }

                    override fun onFailure(call: Call<ServerResponse<User>>, t: Throwable) {
                        loadingDialog.hide()
                    }

                })


        }




        dialog.show()

    }

    private fun addSwitchLister(b: Boolean) {
        if(b){
            binding.switchManager.setOnCheckedChangeListener(managerChangerListener)
        }else{
            binding.switchManager.setOnCheckedChangeListener(null)
        }
    }

    private fun setData(user: User?) {
        setProfilePhoto(user?.photoUrl)



        user?.let {

            it.name?.let {
                binding.name.text = it
            }

            if(!it.email.isNullOrEmpty()){
                binding.enail.visibility = View.VISIBLE
                binding.enail.text = it.email
            }

            if(!it.phone.isNullOrEmpty()){
                binding.phone.visibility = View.VISIBLE
                binding.phone.text = it.phone
            }

            when (it.accType) {
                "2" -> {
                    binding.switchManager.isChecked = true
                    binding.txtUserRole.text = "Manager"
                }
                "1" -> {
                    binding.txtUserRole.text = "User"
                }
                else -> {
                    binding.txtUserRole.text = "Super User"
                }
            }

            it.city?.let {
                binding.txtCity.text = it
            }

            it.country?.let {
                binding.txtCountry.text = Locale("", it).displayName
            }

            it.joinDate?.let {
                binding.txtAccCreatedAt.text = it
            }

            it.gender?.let {
                binding.txtGender.text = it
            }

            it.userName?.let {
                binding.txtUserName.text = it
            }


        }
    }


    private fun pickImg() {
        if(checkPermission()){
            val data = Intent(Intent.ACTION_GET_CONTENT)
            data.addCategory(Intent.CATEGORY_OPENABLE)
            data.type = "image/*"
            val intent = Intent.createChooser(data, "Choose a file")
            pickImg.launch(intent)
        }else{
            askNotificationPermission()
        }
    }

    private fun changeManager(value: Boolean) {
        addSwitchLister(false)
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .changeManager(userProfile!!.id!! , Constant.booleanToInt(value))
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)

                        if(!response.body()!!.error){
                            binding.switchManager.isChecked = value
                        }else{
                            binding.switchManager.isChecked = !value
                        }

                    }

                    addSwitchLister(true)
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                    addSwitchLister(true)
                }

            })


    }

    private fun showChangePasswordDialog() {
        val dialogBinding = DialogPasswordChangeBinding.inflate(layoutInflater);

        val builder= AlertDialog.Builder(this)
            .setCancelable(true)
            .setView(dialogBinding.root)

        val passDialog=builder.create()

        dialogBinding.btnChangePassword.setOnClickListener {
            val oldPass = dialogBinding.edtOldPass.text.toString()
            val newPass1 = dialogBinding.edtNewPass1.text.toString()
            val newPass2 = dialogBinding.edtNewPass2.text.toString()


            if(newPass1 != newPass2){
                shortToast("Password not matched")
                return@setOnClickListener
            }

            if(oldPass.isEmpty()){
                shortToast("Old password must be empty")
                return@setOnClickListener
            }

            if(newPass1.length<6){
                shortToast("New password must be grater than 5 character")
                return@setOnClickListener
            }

            loadingDialog.show()

            (application as MyApplication)
                .myApi
                .changePassword(oldPass, newPass1)
                .enqueue(object : Callback<GenericRespose> {
                    override fun onResponse(
                        call: Call<GenericRespose>,
                        response: Response<GenericRespose>) {

                        loadingDialog.hide()

                        if(response.isSuccessful && response.body()!=null){
                            shortToast(response.body()!!.msg)
                            if(!response.body()!!.error){
                                passDialog.dismiss()
                                startActivity(Intent(this@ProfileActivity, FirstActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                        loadingDialog.hide()
                    }

                })


        }

        passDialog.show()



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return false
    }


    val pickImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //  you will get result here in result.data
            if(result.data!=null) {
                val originalUri: Uri = result.data!!.data!!
                originalUri.let {
                    //shortToast(it.toString())
                    val path = makeFileCopyInCacheDir(it)
                    if(path!=null){
                        uploadImage(path)
                    }else{
                        shortToast("Image Not Selected")
                    }
                }
            }
        }

    }

    private fun uploadImage(path: String) {
        val actualImageFile = File(path)
        Coroutines.main {
            val compressedImageFile = Compressor.compress(this, actualImageFile)
            val fileBody = ProgressRequestBody(compressedImageFile, "image/*",object : ProgressRequestBody.UploadCallbacks {
                override fun onProgressUpdate(percentage: Int) {

                }

                override fun onError() {

                }

                override fun onFinish() {

                }

            })
            val filePart = MultipartBody.Part.createFormData("imageFile", compressedImageFile.getName(), fileBody)

            val res = (application as MyApplication)
                .myApi
                .uploadProfileImage(filePart)

            if(res.isSuccessful && res.body()!=null){
                if(!res.body()!!.error){
                    val user = LocalDB.getUser()
                    user?.let {
                        it.photoUrl = res.body()!!.msg
                        LocalDB.saveUser(it)

                        setProfilePhoto(it.photoUrl)
                    }
                }else{
                    shortToast(res.body()!!.msg)
                }
            }
        }
    }

    private fun setProfilePhoto(photoUrl:String?) {
        photoUrl?.let {
            Glide.with(this)
                .load(com.logicline.mydining.BuildConfig.BASE_URL+it)
                .into(binding.profileImg)
        }

    }

    private fun checkPermission() : Boolean{
        return true
        return this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun askNotificationPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {

                    longToast("Permission granted please select photo again.")

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {



                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?) {

                    token?.continuePermissionRequest()

                }
            }).check()
    }

    private fun makeFileCopyInCacheDir(contentUri :Uri) : String? {
        try {
            val filePathColumn = arrayOf(
                //Base File
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                //Normal File
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DISPLAY_NAME
            )
            //val contentUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", File(mediaUrl))
            val returnCursor = contentUri.let { contentResolver.query(it, filePathColumn, null, null, null) }
            if (returnCursor!=null) {
                returnCursor.moveToFirst()
                val nameIndex = returnCursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                val name = returnCursor.getString(nameIndex)
                val file = File(cacheDir, name)
                val inputStream = contentResolver.openInputStream(contentUri)
                val outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable = inputStream!!.available()

                //int bufferSize = 1024;
                val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
                Log.e("File Path", "Path " + file.path)
                Log.e("File Size", "Size " + file.length())
                return file.absolutePath
            }
        } catch (ex: Exception) {
            Log.e("Exception", ex.message!!)
        }
        return contentUri.let { W_ImgFilePathUtil.getPath(this, it).toString() }
    }



    fun getFilename(): String {
        val file =
            File(Environment.getExternalStorageDirectory().path, "MyFolder/Images")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    private fun getRealPathFromURI(contentURI: String): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor: Cursor? = contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }
}