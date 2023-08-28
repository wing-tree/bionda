package wing.tree.bionda.data.database.type.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.model.LCRiseSetInfo.Item

class LCRiseSetInfoConverters {
    private val json = Json {
        allowStructuredMapKeys = true
    }

    @TypeConverter
    fun itemToString(item: Item): String {
        return json.encodeToString(Item.serializer(), item)
    }

    @TypeConverter
    fun stringToItem(string: String): Item {
        return json.decodeFromString(Item.serializer(), string)
    }
}
