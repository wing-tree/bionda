package wing.tree.bionda.data.database.type.converters

import androidx.room.TypeConverter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.model.Alarm

class AlarmConverters {
    private val json = Json {
        allowStructuredMapKeys = true
    }

    @TypeConverter
    fun conditionsToString(conditions: ImmutableList<Alarm.Condition>): String {
        return json.encodeToString(ListSerializer(Alarm.Condition.serializer()), conditions)
    }

    @TypeConverter
    fun stringToConditions(string: String): ImmutableList<Alarm.Condition> {
        return json
            .decodeFromString(ListSerializer(Alarm.Condition.serializer()), string)
            .toImmutableList()
    }
}