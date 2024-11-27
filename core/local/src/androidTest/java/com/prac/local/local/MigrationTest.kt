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
    fun test_migration_1_to_2() {
        var database = helper.createDatabase(dbName, 1).apply {
            execSQL("INSERT INTO repository VALUES('0', 'test 0', 'test 0', 'test 0', 0, 'test 0', false)")
            close()
        }

        database = helper.runMigrationsAndValidate(dbName, 2, true, MIGRATION_1_2)

        val expectedDefaultBranch = ""
        val cursor = database.query("SELECT * FROM repository")
        cursor.moveToFirst()
        assertEquals(cursor.count, 1)
        assertEquals(cursor.columnCount, 8)
        assertEquals(expectedDefaultBranch, cursor.getString(cursor.getColumnIndex("defaultBranch")))
    }
}