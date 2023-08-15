package wing.tree.bionda.data.database.migration

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

object AutoMigrationSpecs {
    @RenameColumn(tableName = "notice", fromColumnName = "types", toColumnName = "conditions")
    class From1To2 : AutoMigrationSpec
}
