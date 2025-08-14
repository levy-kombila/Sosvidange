package com.ap3.sosvidange

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignalementActivity : AppCompatActivity() {
    private var imageUri : Uri? = null  //les 2 prochains codes sont destinés a utiliser une camera
    private val cameraRequestId = 1222   //on peut metttre n'importe quel chiffre
    private val RESULT_LOAD_IMAGE = 123
    lateinit var cadrePhoto: ImageView  //Creer cette variable en globale pour qu'il puisse etre utilisé par leşautres fonctions
    private lateinit var database: DatabaseReference
    private lateinit var ptNomVille: EditText
    private lateinit var ptNomQuartier: EditText
    private lateinit var mvCom: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signalement)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intentback = Intent(this, PortailActivity::class.java)
            startActivity(intentback)
        }

        val btnCamera: Button = findViewById(R.id.buttonCamera)
        val btnGalleryPhone : Button = findViewById(R.id.buttonGalleryPhone)


        ptNomVille = findViewById(R.id.ptNomVille)
        ptNomQuartier = findViewById(R.id.ptNomQuartier)
        mvCom = findViewById(R.id.mvCom)
        val btnEnvoyer: Button = findViewById(R.id.soumettreBtn)
        btnEnvoyer.setOnClickListener {
            val nomVille = ptNomVille.text.toString()
            val nomQuartier = ptNomQuartier.text.toString()
            val commentaire = mvCom.text.toString()

            // VÉRIFICATION DES CHAMPS OBLIGATOIRES
            if (imageUri == null) {
                errorMessage("Veuillez sélectionner ou prendre une photo.")
                return@setOnClickListener
            }

            if (nomVille.isEmpty()) {
                errorMessage("Le nom de la ville est obligatoire.")
                return@setOnClickListener
            }

            if (nomQuartier.isEmpty()) {
                errorMessage("Le nom du quartier est obligatoire.")
                return@setOnClickListener
            }

            // Tous les champs requis sont valides, on peut uploader
            uploadImageToCloudinary(imageUri)
        }

        cadrePhoto = findViewById(R.id.imageViewPhoto)
        val btnGallery: Button = findViewById(R.id.buttonGalleryPhone)

        database = Firebase.database.reference



        btnCamera.setOnClickListener{ view ->
            openCamera()
        }

        btnGalleryPhone.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, RESULT_LOAD_IMAGE)
        }



    }
    // fonction qui permet d'enregistrer les données dans firebase
    private fun SaveDate(imagesUrl : String?) {
        val nomVille = ptNomVille.text.toString()
        val nomQuartier = ptNomQuartier.text.toString()
        val commentaire = mvCom.text.toString()
        val dateHeure = CurrentDateTime()
        // val image = "ABC123"
        val id = database.push().key!!

        val signalement = Signalement(id, imagesUrl, nomVille, nomQuartier, commentaire,dateHeure)
        database.child("Signalements").child(id).setValue((signalement)).addOnCompleteListener {
            val message = "votre signalement à été envoyer avec succès"
            successMessage(message)
        }.addOnFailureListener{ err ->
            errorMessage(err.message)
        }
        startActivity(Intent(this, SuccessActivity::class.java))
    }

    // fonction qui permet de prendre les informations date
    private fun CurrentDateTime():String? {
        val calendar = Calendar.getInstance().time

        return DateFormat.getDateTimeInstance().format(calendar)
    }

    //Fonction permettant d'afficher des petits messages(toast dans l'application)
    private fun successMessage(message: String?)
    {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG) //Modification de "requireContext().applicationContext" par this
        toast.setGravity(Gravity.START, 200, 200)
        toast.show()
    }
    private fun errorMessage(message : String?)
    {
        val toast = Toast.makeText(this, "Erreur : $message", Toast.LENGTH_LONG) ////Modification de "requireContext().applicationContext" par this
        toast.setGravity(Gravity.START, 200, 200)
        toast.show()
    }

    //TODO opens camera so that user can capture image
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) //Modification de requiredActivity par this
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, cameraRequestId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == cameraRequestId){
            //val image:Bitmap = data?.extras?.get("data") as Bitmap
            //Log.i(TAG, "onActivityResult: ")
            Log.e(TAG, "Example Item: " + data?.data)
            //imageUri  = data?.getStringExtra(MediaStore.EXTRA_OUTPUT) as Uri
            cadrePhoto.setImageURI(imageUri)
            //picker_image.setImageBitmap(image)
            if(imageUri != null) {
                successMessage("image loaded from Camera")
            }
            // extract the file name with extension
        }
        else if(requestCode == RESULT_LOAD_IMAGE)
        {
            imageUri = data?.data
            cadrePhoto.setImageURI(data?.data)
            successMessage("Image loaded from Gallery")
        }

    }

    //Fonction permettant de recuperer le nom du fichier
    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    //fonction qui enregistre les images sur Cloudinary
    private fun uploadImageToCloudinary(imagesUri: Uri?){
        MediaManager.get().upload(imagesUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {

                }

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {

                }

                override fun onSuccess(
                    requestId: String?,
                    resultData: Map<*, *>?
                ) {
                    successMessage("Image uploads successfully")
                    val urlImage = resultData!!["url"] as String?
                    successMessage(urlImage)
                    //mvCom.setText(urlImage)
                    SaveDate(urlImage)
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                    errorMessage("Image upload fail !!!")
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                }

            }).dispatch()
    }

}