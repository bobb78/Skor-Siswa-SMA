package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ParentNotificationLog
import com.example.data.model.ViolationCategory
import com.example.data.model.ViolationMaster
import com.example.data.model.ViolationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ViolationDao {

    // --- Violation Categories ---
    @Query("SELECT * FROM violation_categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<ViolationCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ViolationCategory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(categories: List<ViolationCategory>)

    // --- Violation Masters (Catalog) ---
    @Query("SELECT COUNT(*) FROM violation_masters")
    suspend fun getMasterCount(): Int

    @Query("DELETE FROM violation_masters WHERE isCustom = 0")
    suspend fun deleteDefaultMasters()

    @Query("SELECT * FROM violation_masters ORDER BY points ASC, title ASC")
    fun getAllMasterViolations(): Flow<List<ViolationMaster>>

    @Query("SELECT * FROM violation_masters WHERE categoryId = :categoryId ORDER BY points ASC")
    fun getMasterViolationsByCategory(categoryId: Long): Flow<List<ViolationMaster>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMasterViolation(violation: ViolationMaster): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMasters(masters: List<ViolationMaster>)

    @Update
    suspend fun updateMasterViolation(violation: ViolationMaster)

    @Delete
    suspend fun deleteMasterViolation(violation: ViolationMaster)

    // --- Violation Records (History) ---
    @Query("SELECT * FROM violation_records ORDER BY timestamp DESC")
    fun getAllViolationRecords(): Flow<List<ViolationRecord>>

    @Query("SELECT * FROM violation_records WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getViolationsByStudentId(studentId: Long): Flow<List<ViolationRecord>>

    @Query("SELECT * FROM violation_records WHERE studentId = :studentId ORDER BY timestamp DESC LIMIT 25")
    suspend fun getCachedViolationsForStudent(studentId: Long): List<ViolationRecord>

    @Query("SELECT * FROM violation_records ORDER BY timestamp DESC LIMIT 30")
    suspend fun getRecentCachedViolations(): List<ViolationRecord>

    @Query("SELECT * FROM violation_records WHERE studentClass = :className ORDER BY timestamp DESC")
    fun getViolationsByClass(className: String): Flow<List<ViolationRecord>>

    @Query("SELECT * FROM violation_records WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getViolationsSince(startTime: Long): Flow<List<ViolationRecord>>

    @Query("SELECT SUM(points) FROM violation_records WHERE studentId = :studentId")
    suspend fun calculateStudentTotalPoints(studentId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolationRecord(record: ViolationRecord): Long

    @Update
    suspend fun updateViolationRecord(record: ViolationRecord)

    @Query("UPDATE violation_records SET parentNotified = 1, parentNotificationType = :type, parentNotificationDate = :timestamp WHERE id = :recordId")
    suspend fun markParentNotified(recordId: Long, type: String, timestamp: Long)

    @Delete
    suspend fun deleteViolationRecord(record: ViolationRecord)

    @Query("DELETE FROM violation_records WHERE id = :recordId")
    suspend fun deleteViolationRecordById(recordId: Long)

    @Query("DELETE FROM violation_records")
    suspend fun deleteAllRecords()

    // --- Parent Notification Logs ---
    @Query("SELECT * FROM parent_notification_logs ORDER BY timestamp DESC")
    fun getAllNotificationLogs(): Flow<List<ParentNotificationLog>>

    @Query("SELECT * FROM parent_notification_logs WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getLogsByStudentId(studentId: Long): Flow<List<ParentNotificationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationLog(log: ParentNotificationLog): Long

    @Query("SELECT COUNT(*) FROM parent_notification_logs")
    fun getTotalNotificationCount(): Flow<Int>

    @Query("DELETE FROM parent_notification_logs")
    suspend fun deleteAllNotificationLogs()
}
