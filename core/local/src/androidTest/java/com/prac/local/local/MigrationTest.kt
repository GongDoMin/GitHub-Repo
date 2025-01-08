package com.prac.local.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.local.room.database.RepositoryDatabase
import com.prac.local.room.migration.MIGRATION_1_2
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val dbName = "repositoryDB"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RepositoryDatabase::class.java
    )

    @Test
    fun 데이터베이스_1에서_2로_마이그레이션_테스트() {
        // given
        val expectedCount = 1
        val expectedColumnCount = 8
        val expectedDefaultBranch = ""
        var database = helper.createDatabase(dbName, 1).apply {
            execSQL("INSERT INTO repository VALUES('0', 'test 0', 'test 0', 'test 0', 0, 'test 0', false)")
            close()
        }

        // when
        database = helper.runMigrationsAndValidate(dbName, 2, true, MIGRATION_1_2)

        // then
        val cursor = database.query("SELECT * FROM repository").apply { moveToFirst() }
        assertEquals(cursor.count, expectedCount)
        assertEquals(cursor.columnCount, expectedColumnCount)
        assertEquals(cursor.getString(cursor.getColumnIndex("defaultBranch")), expectedDefaultBranch)
    }
}