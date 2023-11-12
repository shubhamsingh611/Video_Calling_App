package com.example.videocallapp.fragment

import android.content.Intent
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.example.videocallapp.MainActivity
import com.example.videocallapp.R
import com.example.videocallapp.activity.HomeActivity
import com.example.videocallapp.databinding.FragmentLoginBinding
import com.example.videocallapp.databinding.FragmentLoginVerificationBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginVerificationFragment : Fragment() {
    private lateinit var binding: FragmentLoginVerificationBinding
    private var getOtpBackend : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login_verification,
            container,
            false
        )

        //Log.d("Back End OTP" ,  "on Create new - "+arguments?.getString("mobile"))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments

        val mobileNumber: String? = String.format("+91-%s", bundle?.getBundle("mobile")?.getString("mobile"))
            //"+91"+bundle?.getBundle("mobile")?.getString("mobile")
        getOtpBackend = arguments?.getString("backendotp")

        Log.d("Back End OTP " , getOtpBackend.toString()+ "new - "+arguments?.getString("mobile"))

        binding.verifyButton.setOnClickListener {
            if (binding.editTextNumber1.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber2.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber3.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber4.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber5.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber6.text.toString().trim().isNotEmpty()
            )   {

                var enteredOtp: String = binding.editTextNumber1.text.toString() +
                        binding.editTextNumber2.text.toString() +
                        binding.editTextNumber3.text.toString() +
                        binding.editTextNumber4.text.toString() +
                        binding.editTextNumber5.text.toString() +
                        binding.editTextNumber6.text.toString()

                if (getOtpBackend != null) {
                    binding.verifyOtpProgressBar.visibility = View.VISIBLE
                    binding.verifyButton.visibility = View.INVISIBLE

                    Log.d("Get OTP Backend ",getOtpBackend.toString())
                    Log.d("Entered OTP ",enteredOtp)

                    val phoneAuthCredential = PhoneAuthProvider.getCredential(
                        getOtpBackend!!, enteredOtp
                    )


                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            binding.verifyOtpProgressBar.visibility = View.GONE
                            binding.verifyButton.visibility = View.VISIBLE
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Authentication Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(activity, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Enter the Correct OTP",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        activity,
                        "Please Check your Internet Connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }
//                Toast.makeText(activity,"OTP Verify",Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(activity, "Please Enter All Numbers", Toast.LENGTH_SHORT).show()

            }
        }
            otpNumbersMove()
        binding.tvResendOtp.setOnClickListener{
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91$mobileNumber",60, TimeUnit.SECONDS,this.requireActivity(),
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {

                        Toast.makeText(activity,p0.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(
                        newBackEndOtp : String,
                        p1: PhoneAuthProvider.ForceResendingToken
                    ) {
                        getOtpBackend = newBackEndOtp
                        Toast.makeText(activity,"OTP Sent Successfully",Toast.LENGTH_SHORT).show()
                    }

                }
            )
        }

    }

    private fun otpNumbersMove(){
        binding.let{
            cursorMove(it.editTextNumber1,it.editTextNumber2)
            cursorMove(it.editTextNumber2,it.editTextNumber3)
            cursorMove(it.editTextNumber3,it.editTextNumber4)
            cursorMove(it.editTextNumber4,it.editTextNumber5)
            cursorMove(it.editTextNumber5,it.editTextNumber6)
        }
    }

    private fun cursorMove(editTextStart : EditText, editTextEnd : EditText){
        editTextStart.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().trim().isNotEmpty()){
                    editTextEnd.requestFocus()
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

}