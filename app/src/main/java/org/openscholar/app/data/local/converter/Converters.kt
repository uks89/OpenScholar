package org.openscholar.app.data.local.converter

import androidx.room.TypeConverter
import org.openscholar.app.model.FileType
import org.openscholar.app.model.MemoryType
import org.openscholar.app.model.MessageRole
import org.openscholar.app.model.NodeType
import org.openscholar.app.model.RelationshipType

class Converters {

    @TypeConverter
    fun fromFileType(value: FileType): String = value.name

    @TypeConverter
    fun toFileType(value: String): FileType = FileType.valueOf(value)

    @TypeConverter
    fun fromNodeType(value: NodeType): String = value.name

    @TypeConverter
    fun toNodeType(value: String): NodeType = NodeType.valueOf(value)

    @TypeConverter
    fun fromRelationshipType(value: RelationshipType): String = value.name

    @TypeConverter
    fun toRelationshipType(value: String): RelationshipType = RelationshipType.valueOf(value)

    @TypeConverter
    fun fromMemoryType(value: MemoryType): String = value.name

    @TypeConverter
    fun toMemoryType(value: String): MemoryType = MemoryType.valueOf(value)

    @TypeConverter
    fun fromMessageRole(value: MessageRole): String = value.name

    @TypeConverter
    fun toMessageRole(value: String): MessageRole = MessageRole.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(",").map { it.trim() }
}
