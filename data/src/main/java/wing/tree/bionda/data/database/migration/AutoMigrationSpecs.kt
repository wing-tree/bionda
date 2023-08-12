package wing.tree.bionda.data.database.migration

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

object AutoMigrationSpecs {
    @RenameColumn(tableName = "notice", fromColumnName = "notificationId", toColumnName = "id")
    class Schema1To2 : AutoMigrationSpec

    @RenameColumn(tableName = "notice", fromColumnName = "checked", toColumnName = "on")
    class Schema2To3 : AutoMigrationSpec
}
