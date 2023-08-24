package wing.tree.bionda.data.database.type.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.model.weather.MidTa

class MidTaConverters {
    private val json = Json {
        allowStructuredMapKeys = true
    }

    @TypeConverter
    fun itemToString(item: MidTa.Item): String {
        return json.encodeToString(MidTa.Item.serializer(), item)
    }

    @TypeConverter
    fun stringToItem(string: String): MidTa.Item {
        return json.decodeFromString(MidTa.Item.serializer(), string)
    }
}
