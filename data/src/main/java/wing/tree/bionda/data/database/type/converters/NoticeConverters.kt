package wing.tree.bionda.data.database.type.converters

import androidx.room.TypeConverter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.model.Notice

class NoticeConverters {
    private val json = Json {
        allowStructuredMapKeys = true
    }

    @TypeConverter
    fun conditionsToString(conditions: ImmutableList<Notice.Condition>): String {
        return json.encodeToString(ListSerializer(Notice.Condition.serializer()), conditions)
    }

    @TypeConverter
    fun stringToConditions(string: String): ImmutableList<Notice.Condition> {
        return json
            .decodeFromString(ListSerializer(Notice.Condition.serializer()), string)
            .toImmutableList()
    }
}