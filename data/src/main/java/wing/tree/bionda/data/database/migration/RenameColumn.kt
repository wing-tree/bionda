package wing.tree.bionda.data.database.migration

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

@RenameColumn(tableName = "notice", fromColumnName = "notificationId", toColumnName = "id")
class RenameColumn : AutoMigrationSpec
