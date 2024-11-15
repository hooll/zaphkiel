package ink.ptms.zaphkiel.impl.meta

import dev.lone.itemsadder.api.CustomStack
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent
import dev.lone.itemsadder.api.ItemsAdder
import ink.ptms.zaphkiel.Zaphkiel
import ink.ptms.zaphkiel.api.ItemKey
import ink.ptms.zaphkiel.api.event.ItemBuildEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.item.meta.Meta
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.platform.util.modifyMeta
import taboolib.platform.util.setMeta

@MetaKey("itemsadder")
class MetaItemsadder(root: ConfigurationSection) : Meta(root) {

    companion object{

        @SubscribeEvent
        private fun onIALoad(e: ItemsAdderLoadDataEvent){
            Zaphkiel.api().reload()
        }
    }

    val itemsadder = root.getString("meta.itemsadder")?.run { CustomStack.getInstance(this) }
    val iaItemstack = itemsadder?.itemStack

    override val id: String
        get() = "itemsadder"

    override fun build(player: Player?, compound: ItemTag) {
        if (iaItemstack == null) return
        compound["itemsadder"] = ItemTag().apply {
            put("namespace",ItemTagData(itemsadder!!.namespace))
            put("id",ItemTagData(itemsadder.id))
        }
    }

    override fun build(itemReleaseEvent: ItemReleaseEvent) {
        if (iaItemstack == null) return
        itemReleaseEvent.icon = iaItemstack.type
        if (!iaItemstack.hasItemMeta()) return
        iaItemstack.itemMeta!!.apply {
            itemReleaseEvent.itemMeta.setCustomModelData(this.customModelData)
        }

    }

    override fun build(itemMeta: ItemMeta) {
        if (iaItemstack == null) return
        if (itemMeta is PotionMeta) {
            itemMeta.color = (iaItemstack.itemMeta as PotionMeta).color
        } else if (itemMeta is LeatherArmorMeta) {
            itemMeta.setColor((iaItemstack.itemMeta as LeatherArmorMeta).color)
        }
    }

    override fun drop(itemMeta: ItemMeta) {
        if (itemMeta is PotionMeta) {
            itemMeta.color = null
        } else if (itemMeta is LeatherArmorMeta) {
            itemMeta.setColor(null)
        }
    }

    override fun drop(player: Player?, compound: ItemTag) {
        compound.removeDeep("itemsadder")
    }

    override fun drop(itemReleaseEvent: ItemReleaseEvent) {
        itemReleaseEvent.icon = itemReleaseEvent.item.icon.type
        itemReleaseEvent.itemMeta.setCustomModelData(null)
    }

    override fun toString(): String {
        return "MetaItemsadder(itemsadder=${itemsadder})"
    }

}