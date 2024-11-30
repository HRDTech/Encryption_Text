package com.solucioneshr.soft.encryptiontext

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.solucioneshr.soft.encryptiontext.databinding.ActivityMainBinding
import javax.crypto.SecretKey
import kotlin.io.encoding.ExperimentalEncodingApi

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonEDBMode.setOnClickListener {
            showCipherDialog("ECB")
        }

        binding.buttonCBCMode.setOnClickListener {
            showCipherDialog("CBC")
        }

        binding.buttonCTRMode.setOnClickListener {
            showCipherDialog("CTR")
        }

        binding.buttonGCMMode.setOnClickListener {
            showCipherDialog("GCM")
        }

    }

    @SuppressLint("MissingInflatedId", "CutPasteId")
    private fun showCipherDialog (cipherMode: String) {
        val viewDialog = layoutInflater.inflate(R.layout.cipher_dialog_layout, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()

        when (cipherMode) {
            "ECB" -> {
                viewDialog.findViewById<TextView>(R.id.textTitleECBMode).visibility = View.VISIBLE
            }
            "CBC" -> {
                viewDialog.findViewById<TextView>(R.id.textTitleCBCMode).visibility = View.VISIBLE
            }
            "CTR" -> {
                viewDialog.findViewById<TextView>(R.id.textTitleCTRMode).visibility =
                    View.VISIBLE
            }
            "GCM" -> {
                viewDialog.findViewById<TextView>(R.id.textTitleGCMMode).visibility = View.VISIBLE
            }
        }

        val textKey = viewDialog.findViewById<TextInputEditText>(R.id.dialogKey)
        val layoutKey = viewDialog.findViewById<TextInputLayout>(R.id.dialogLayoutKey)
        val buttonGenerateKey = viewDialog.findViewById<ImageButton>(R.id.buttonGenerateKey)
        val textEncrypt = viewDialog.findViewById<TextInputEditText>(R.id.dialogEncrypt)
        val textDecrypt = viewDialog.findViewById<TextInputEditText>(R.id.dialogDecrypt)
        val textViewDecrypt = viewDialog.findViewById<TextInputEditText>(R.id.dialogDecipher)

        textKey.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when (count) {
                    16 -> {
                        layoutKey.error = "AES - 128"
                    }
                    24 -> {
                        layoutKey.error = "AES - 192"
                    }
                    32 -> {
                        layoutKey.error = "AES - 256"
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        
        buttonGenerateKey.setOnClickListener {
            val key = CipherAES().generateAESKey()
            val keyBase64 = Base64.encodeToString(key.encoded, Base64.DEFAULT)
            if (keyBase64.length > 32) {
                textKey.setText(keyBase64.subSequence(0, 32))
            } else {
                textKey.setText(keyBase64)
            }
        }

        val buttonDialogClose = viewDialog.findViewById<ImageButton>(R.id.buttonDialogClose)
        buttonDialogClose.setOnClickListener {
            dialog.dismiss()
        }

        val buttonEncrypt = viewDialog.findViewById<Button>(R.id.dialogButtonEncrypt)
        buttonEncrypt.setOnClickListener {
            when (cipherMode) {
                "ECB" -> {
                    val textCipher = CipherAES().encryptAESECBMode(textEncrypt.text.toString(), textKey.text.toString())
                    textDecrypt.setText(textCipher)
                }
                "CBC" -> {
                    val iv = "InitializationVe" // 16 bytes
                    val textCipher = CipherAES().encryptAESCBCMode(textEncrypt.text.toString(), textKey.text.toString(), iv)
                    textDecrypt.setText(textCipher)
                }
                "CTR" -> {
                    val key = textKey.text.toString().subSequence(0, 16).toString().toByteArray()
                    val iv = "Your16ByteIVHere".toByteArray()
                    val textCipher = CipherAES().encryptAESCTRMode(textEncrypt.text.toString(), key, iv)
                    textDecrypt.setText(textCipher)
                }
                "GCM" -> {
                    val key = textKey.text.toString().subSequence(0, 16).toString().toByteArray()
                    val textCipher = CipherAES().encryptAESGCMMode(textEncrypt.text.toString(), key)
                    textDecrypt.setText(textCipher)
                }
            }
        }

        val buttonDecrypt = viewDialog.findViewById<Button>(R.id.dialogButtonDecrypt)
        buttonDecrypt.setOnClickListener {
            when (cipherMode) {
                "ECB" -> {
                    val textDecipher = CipherAES().decryptAESECBMode(textDecrypt.text.toString(), textKey.text.toString())
                    textViewDecrypt.setText(textDecipher)
                }
                "CBC" -> {
                    val iv = "InitializationVe" // 16 bytes
                    val textDecipher = CipherAES().decryptAESCBCMode(textDecrypt.text.toString(), textKey.text.toString(), iv)
                    textViewDecrypt.setText(textDecipher)
                }
                "CTR" -> {
                    val key = textKey.text.toString().subSequence(0, 16).toString().toByteArray()
                    val iv = "Your16ByteIVHere".toByteArray()
                    val textDecipher = CipherAES().decryptAESCTRMode(textDecrypt.text.toString(), key, iv)
                    textViewDecrypt.setText(textDecipher)
                }
                "GCM" -> {
                    val key = textKey.text.toString().subSequence(0, 16).toString().toByteArray()
                    val textDecipher = CipherAES().decryptAESGCMMode(textDecrypt.text.toString(), key)
                    textViewDecrypt.setText(textDecipher)
                }
            }
        }

        dialog.setView(viewDialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
}