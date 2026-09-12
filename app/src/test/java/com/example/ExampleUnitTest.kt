package com.example

import com.example.data.model.UserRole
import com.example.data.model.UserSession
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testStudentRoleCannotModifyPoints() {
        val studentSession = UserSession(
            isLoggedIn = true,
            role = UserRole.SISWA,
            name = "Budi Pratama",
            identifier = "1001"
        )
        assertFalse(studentSession.canModifyPoints)
        assertFalse(studentSession.canManageStudents)
        assertFalse(studentSession.canDeleteRecords)
        assertFalse(studentSession.canSendNotifications)
        assertFalse(studentSession.canEditSchoolProfile)
    }

    @Test
    fun testTeacherAndCounselorCanModifyPoints() {
        val teacherSession = UserSession(
            isLoggedIn = true,
            role = UserRole.GURU,
            name = "Pak Ahmad, S.Pd",
            identifier = "GURU-PIKET"
        )
        assertTrue(teacherSession.canModifyPoints)
        assertTrue(teacherSession.canManageStudents)
        assertTrue(teacherSession.canSendNotifications)

        val counselorSession = UserSession(
            isLoggedIn = true,
            role = UserRole.GURU_BK,
            name = "Ibu Siti, M.Pd (BK)",
            identifier = "GURU-BK"
        )
        assertTrue(counselorSession.canModifyPoints)
        assertTrue(counselorSession.canManageStudents)
        assertTrue(counselorSession.canDeleteRecords)
        assertTrue(counselorSession.canSendNotifications)
        assertTrue(counselorSession.canEditSchoolProfile)
    }
}
