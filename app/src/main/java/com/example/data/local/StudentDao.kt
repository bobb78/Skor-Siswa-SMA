package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY totalScore DESC, name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): Student?

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentFlowById(id: Long): Flow<Student?>

    @Query("SELECT * FROM students WHERE className = :className ORDER BY totalScore DESC, name ASC")
    fun getStudentsByClass(className: String): Flow<List<Student>>

    @Query("SELECT DISTINCT className FROM students ORDER BY className ASC")
    fun getAllClassNames(): Flow<List<String>>

    @Query("SELECT * FROM students WHERE totalScore >= 25 ORDER BY totalScore DESC")
    fun getAtRiskStudents(): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students")
    fun getTotalStudentCount(): Flow<Int>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' OR nis LIKE '%' || :query || '%' LIMIT 10")
    suspend fun searchStudentsByNameOrNis(query: String): List<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("UPDATE students SET totalScore = :newScore, lastViolationDate = :lastDate WHERE id = :studentId")
    suspend fun updateStudentScore(studentId: Long, newScore: Int, lastDate: Long)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)

    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}
