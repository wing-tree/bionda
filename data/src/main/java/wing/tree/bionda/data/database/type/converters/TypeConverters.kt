package wing.tree.bionda.data.database.type.converters

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.model.forecast.Item

class TypeConverters {
    private val json = Json {
        allowStructuredMapKeys = true
    }

    @TypeConverter
    fun itemsToString(items: List<Item>): String {
        return json.encodeToString(ListSerializer(Item.serializer()), items)
    }

    @TypeConverter
    fun stringToItems(string: String): List<Item> {
        return json.decodeFromString(ListSerializer(Item.serializer()), string)
    }
}
