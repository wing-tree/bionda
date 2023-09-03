package wing.tree.bionda.data.database.type.converters

import androidx.room.TypeConverter
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.model.VilageFcst.Item

class VilageFcstConverters {
    private val json = Json {
        allowStructuredMapKeys = true
    }

    @TypeConverter
    fun itemsToString(items: PersistentList<Item>): String {
        return json.encodeToString(ListSerializer(Item.serializer()), items)
    }

    @TypeConverter
    fun stringToItems(string: String): PersistentList<Item> {
        return json
            .decodeFromString(ListSerializer(Item.serializer()), string)
            .toPersistentList()
    }
}
