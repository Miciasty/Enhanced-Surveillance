
# Minecraft 1.20.6 Event List

## Player Events (Zdarzenia Gracza)
- PlayerJoinEvent: Triggered when a player joins the server.
- PlayerQuitEvent: Triggered when a player leaves the server.
- PlayerMoveEvent: Triggered when a player moves.
- PlayerInteractEvent: Triggered when a player interacts with a block or item.
- PlayerChatEvent: Triggered when a player sends a message in the chat.
- PlayerDeathEvent: Triggered when a player dies.
- PlayerRespawnEvent: Triggered when a player respawns.
- PlayerTeleportEvent: Triggered when a player teleports.
- PlayerKickEvent: Triggered when a player is kicked from the server.
- PlayerToggleSneakEvent: Triggered when a player toggles sneak mode.
- PlayerToggleSprintEvent: Triggered when a player toggles sprint mode.
- PlayerDropItemEvent: Triggered when a player drops an item.
- PlayerItemConsumeEvent: Triggered when a player consumes an item.
- PlayerBucketFillEvent: Triggered when a player fills a bucket.
- PlayerBucketEmptyEvent: Triggered when a player empties a bucket.
- PlayerShearEntityEvent: Triggered when a player shears an entity.
- PlayerFishEvent: Triggered when a player is fishing.

## Block Events (Zdarzenia Bloków)
- BlockBreakEvent: Triggered when a block is broken by a player.
- BlockPlaceEvent: Triggered when a block is placed by a player.
- BlockGrowEvent: Triggered when a block grows (e.g., tree or plant).
- BlockExplodeEvent: Triggered when a block is destroyed by an explosion.
- BlockBurnEvent: Triggered when a block is destroyed by fire.
- BlockFormEvent: Triggered when a block forms naturally (e.g., ice forming).
- BlockFadeEvent: Triggered when a block fades (e.g., ice melting).
- BlockFromToEvent: Triggered when a block changes to another type (e.g., water flowing).
- BlockDamageEvent: Triggered when a block is damaged by a player.
- BlockDispenseEvent: Triggered when a block with an inventory (e.g., dispenser) dispenses an item.
- BlockRedstoneEvent: Triggered when a block powered by redstone changes state.
- BlockIgniteEvent: Triggered when a block is ignited.

## Entity Events (Zdarzenia Jednostek)
- EntityDamageEvent: Triggered when an entity (e.g., player, mob) takes damage.
- EntityDeathEvent: Triggered when an entity dies.
- EntitySpawnEvent: Triggered when an entity is spawned in the world.
- EntityTameEvent: Triggered when an entity is tamed by a player.
- EntityRegainHealthEvent: Triggered when an entity regains health.
- EntityExplodeEvent: Triggered when an entity explodes (e.g., creeper).
- EntityChangeBlockEvent: Triggered when an entity changes a block (e.g., enderman).
- EntityCombustEvent: Triggered when an entity catches fire.
- EntityPortalEvent: Triggered when an entity enters a portal.
- EntityShootBowEvent: Triggered when an entity shoots a bow.
- EntityPotionEffectEvent: Triggered when an entity is affected by a potion.

## Inventory Events (Zdarzenia Ekwipunku)
- InventoryClickEvent: Triggered when a player clicks in an inventory.
- InventoryOpenEvent: Triggered when a player opens an inventory.
- InventoryCloseEvent: Triggered when a player closes an inventory.
- InventoryMoveItemEvent: Triggered when an item is moved in an inventory.
- InventoryDragEvent: Triggered when an item is dragged in an inventory.
- InventoryPickupItemEvent: Triggered when an inventory picks up an item.

## Weather Events (Zdarzenia Pogodowe)
- WeatherChangeEvent: Triggered when the weather changes in the world.
- ThunderChangeEvent: Triggered when the state of thunder changes in the world.

## World Events (Zdarzenia Świata)
- WorldLoadEvent: Triggered when a world is loaded.
- WorldUnloadEvent: Triggered when a world is unloaded.
- WorldInitEvent: Triggered when a world is initialized.
- WorldSaveEvent: Triggered when a world is saved.

## Server Events (Zdarzenia Serwera)
- ServerLoadEvent: Triggered when the server is loaded.
- ServerCommandEvent: Triggered when a command is executed on the server.
- ServerListPingEvent: Triggered when the server receives a ping for the server list.
- ServerStopEvent: Triggered when the server is stopped.
- ServerTickEvent: Triggered every server tick.
