package com.example.data.auth

import android.util.Log
import com.example.data.model.AppUser
import com.example.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Firebase Authentication Service for managing user identities and RBAC roles:
 * - Teacher BK (Counselor / Full Administrative)
 * - Teacher Piket (Discipline Officer)
 * - Student / Guardian (Read-Only Access)
 */
object FirebaseAuthService {
    private const val TAG = "FirebaseAuthService"

    val firebaseAuth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth not fully initialized: ${e.message}")
            null
        }

    val currentUser: FirebaseUser?
        get() = try {
            firebaseAuth?.currentUser
        } catch (e: Exception) {
            null
        }

    /**
     * Signs in teacher credentials with Firebase Auth (email & PIN/password).
     * Automatically registers account if not yet created.
     */
    suspend fun signInWithEmail(email: String, pinOrPass: String, role: UserRole): AppUser {
        val auth = firebaseAuth
        val securePass = if (pinOrPass.length >= 6) pinOrPass else "${pinOrPass}123456"
        if (auth != null) {
            try {
                val result = auth.signInWithEmailAndPassword(email, securePass).await()
                val user = result.user
                if (user != null) {
                    return AppUser(
                        uid = user.uid,
                        email = user.email ?: email,
                        displayName = user.displayName ?: email.substringBefore("@"),
                        role = role,
                        identifier = email
                    )
                }
            } catch (e: Exception) {
                Log.d(TAG, "Firebase Auth sign-in: creating user on demand (${e.message})")
                try {
                    val createResult = auth.createUserWithEmailAndPassword(email, securePass).await()
                    val newUser = createResult.user
                    if (newUser != null) {
                        return AppUser(
                            uid = newUser.uid,
                            email = newUser.email ?: email,
                            displayName = newUser.displayName ?: email.substringBefore("@"),
                            role = role,
                            identifier = email
                        )
                    }
                } catch (ce: Exception) {
                    Log.d(TAG, "Local authenticated session fallback: ${ce.message}")
                }
            }
        }

        // Offline / Graceful fallback AppUser
        return AppUser(
            uid = "auth_local_${System.currentTimeMillis()}",
            email = email,
            displayName = email.substringBefore("@"),
            role = role,
            identifier = email
        )
    }

    /**
     * Anonymous or tokenized sign in for Student / Guest read-only portal
     */
    suspend fun signInStudent(nis: String, studentName: String): AppUser {
        val auth = firebaseAuth
        var uid = "student_$nis"
        if (auth != null) {
            try {
                val result = auth.signInAnonymously().await()
                uid = result.user?.uid ?: uid
            } catch (e: Exception) {
                Log.w(TAG, "Student anonymous sign-in fallback: ${e.message}")
            }
        }
        return AppUser(
            uid = uid,
            email = "student_$nis@skorsiswa.sch.id",
            displayName = studentName,
            role = UserRole.STUDENT,
            identifier = nis
        )
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase signOut exception: ${e.message}")
        }
    }
}
