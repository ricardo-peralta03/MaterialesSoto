package mx.tecnm.cdhidalgo.materialessoto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import mx.tecnm.cdhidalgo.materialessoto.dataclass.Usuario

class Login : AppCompatActivity() {
    private lateinit var correo: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var btn_ingresar: MaterialButton
    private lateinit var btn_registrar: MaterialButton
    private lateinit var btn_olvidar: MaterialButton
    private lateinit var auth: FirebaseAuth
    private lateinit var usuario: Usuario


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore


        correo = findViewById(R.id.correo_login)
        password = findViewById(R.id.password_login)
        btn_ingresar = findViewById(R.id.btn_login)
        btn_registrar = findViewById(R.id.btn_registrar)
        btn_olvidar = findViewById(R.id.btn_olvidar)

        btn_ingresar.setOnClickListener {
            val email = correo.editText?.text.toString()
            val pass = password.editText?.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty())
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        db.collection("Usuarios").whereEqualTo("correo", email).get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    usuario = Usuario(
                                        document.data["correo"].toString(),
                                        document.data["nombre"].toString(),
                                        document.data["apaterno"].toString(),
                                        document.data["amaterno"].toString(),
                                    )
                                }
                                val intent = Intent(this, materiales::class.java)
                                intent.putExtra("Usuario", usuario)
                                startActivity(intent)
                            }

                    }else{
                        showAlert()
                    }
                }

        }
        btn_registrar.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
        btn_olvidar.setOnClickListener {
            val intent = Intent(this, RecuperaPassword::class.java)
            startActivity(intent)
        }

    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}