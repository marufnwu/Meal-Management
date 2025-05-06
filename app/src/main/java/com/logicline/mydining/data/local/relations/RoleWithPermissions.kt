package com.logicline.mydining.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.logicline.mydining.data.local.entities.PermissionEntity
import com.logicline.mydining.data.local.entities.RoleEntity

data class RoleWithPermissions(
    @Embedded val role: RoleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "messRoleId"
    )
    val permissions: List<PermissionEntity>
)
