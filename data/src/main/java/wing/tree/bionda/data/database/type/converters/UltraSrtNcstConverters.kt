package wing.tree.bionda.data.database.type.converters

import androidx.room.TypeConverter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.model.UltraSrtNcst.Item

class UltraSrtNcstConverters {
    private val json = Json {
        allowStructuredMapKeys = true
    }

    @TypeConverter
    fun itemsToString(items: ImmutableList<Item>): String {
        return json.encodeToString(ListSerializer(Item.serializer()), items)
    }

    @TypeConverter
    fun stringToItems(string: String): ImmutableList<Item> {
        return json
            .decodeFromString(ListSerializer(Item.serializer()), string)
            .toImmutableList()
    }
}
