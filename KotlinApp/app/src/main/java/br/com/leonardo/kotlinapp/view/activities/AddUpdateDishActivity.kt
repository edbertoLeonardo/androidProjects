package br.com.leonardo.kotlinapp.view.activities

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.leonardo.kotlinapp.R
import br.com.leonardo.kotlinapp.databinding.ActivityAddUpdateDishBinding
import br.com.leonardo.kotlinapp.databinding.DialogCustomImageSelectionBinding
import com.karumi.dexter.Dexter
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupActionBar()

        mBinding.ivAddDishImage.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_add_dish_image ->{
                    customImageSelectionDialog()
                    return
                }

            }

        }

    }


    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@AddUpdateDishActivity)

        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)


        dialog.setContentView(binding.root)

        // TODO Step 7: Assign the click for Camera and Gallery. Show the Toast message for now.
        // START
        binding.tvCamera.setOnClickListener {

            Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
               // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .withListener(object: MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }//else{
//                            showRationalDialogForPermissions()
//                        }
                        }
                        }


                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                       showRationalDialogForPermissions()

                    }

                }).onSameThread().check()

            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        Toast.makeText(this@AddUpdateDishActivity, "Vocẽ tem permissão para escolher uma imagem", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(this@AddUpdateDishActivity, "Vocẽ não tem permissão para escolher uma imagem", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }


                }).onSameThread().check()

            dialog.dismiss()
        }

        dialog.show()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Activity.RESULT_OK) {
//            data?.extras?.let {
//                val thumbnail = data.extras!!["data"] as Bitmap
//                mBinding.ivDishImage.setImageBitmap(thumbnail)
//                mBinding.ivAddDishImage
//                    .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit))
//            }
//        }
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA){
                data?.extras?.let{
                    val thumbnail : Bitmap = data.extras!!.get("data") as Bitmap
                    mBinding.ivAddDishImage.setImageBitmap(thumbnail)
                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit))
                }

            }
        }

    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("Verifique as permissões do app").setPositiveButton("Configurações/Permissões"){
            _,_ -> try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
            startActivity(intent)

            }catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }

            .setNegativeButton("Cancelar"){
                dialog, _ -> dialog.dismiss()
            }.show()
    }

    companion object{
        private const val CAMERA = 1
    }
}