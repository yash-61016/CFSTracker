package com.teessideUni.cfs_tracker.domain.repository

class ValidationUtils {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        fun isValidName(name: String): Boolean {
            return name.isNotEmpty() && name.matches("[a-zA-Z ]+".toRegex())
        }
        fun isValidPassword(password: String): Boolean {
            return password.length >= 8 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@$!%*?&]{8,}\$".toRegex())
        }

        fun isValidPhoneNumber(phoneNumber: String): Boolean {
            return phoneNumber.isNotEmpty() && phoneNumber.matches("^\\+[0-9]{10,13}\$".toRegex())
        }

        fun isValidCurrentPassword(currentPassword: String, password: String): Boolean {
            return password.length >= 8 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@$!%*?&]{8,}\$".toRegex()) &&  currentPassword == password
        }
    }
}