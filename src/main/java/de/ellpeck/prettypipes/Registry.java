package de.ellpeck.prettypipes;

import de.ellpeck.prettypipes.entities.PipeFrameEntity;
import de.ellpeck.prettypipes.entities.PipeFrameRenderer;
import de.ellpeck.prettypipes.items.*;
import de.ellpeck.prettypipes.misc.ItemEquality;
import de.ellpeck.prettypipes.network.PipeNetwork;
import de.ellpeck.prettypipes.packets.PacketHandler;
import de.ellpeck.prettypipes.pipe.IPipeConnectable;
import de.ellpeck.prettypipes.pipe.PipeBlock;
import de.ellpeck.prettypipes.pipe.PipeBlockEntity;
import de.ellpeck.prettypipes.pipe.PipeRenderer;
import de.ellpeck.prettypipes.pipe.containers.AbstractPipeContainer;
import de.ellpeck.prettypipes.pipe.containers.MainPipeContainer;
import de.ellpeck.prettypipes.pipe.containers.MainPipeGui;
import de.ellpeck.prettypipes.pipe.modules.*;
import de.ellpeck.prettypipes.pipe.modules.craft.CraftingModuleContainer;
import de.ellpeck.prettypipes.pipe.modules.craft.CraftingModuleGui;
import de.ellpeck.prettypipes.pipe.modules.craft.CraftingModuleItem;
import de.ellpeck.prettypipes.pipe.modules.extraction.ExtractionModuleContainer;
import de.ellpeck.prettypipes.pipe.modules.extraction.ExtractionModuleGui;
import de.ellpeck.prettypipes.pipe.modules.extraction.ExtractionModuleItem;
import de.ellpeck.prettypipes.pipe.modules.filter.FilterIncreaseModuleContainer;
import de.ellpeck.prettypipes.pipe.modules.filter.FilterIncreaseModuleGui;
import de.ellpeck.prettypipes.pipe.modules.filter.FilterIncreaseModuleItem;
import de.ellpeck.prettypipes.pipe.modules.insertion.FilterModuleContainer;
import de.ellpeck.prettypipes.pipe.modules.insertion.FilterModuleGui;
import de.ellpeck.prettypipes.pipe.modules.insertion.FilterModuleItem;
import de.ellpeck.prettypipes.pipe.modules.modifier.FilterModifierModuleContainer;
import de.ellpeck.prettypipes.pipe.modules.modifier.FilterModifierModuleGui;
import de.ellpeck.prettypipes.pipe.modules.modifier.FilterModifierModuleItem;
import de.ellpeck.prettypipes.pipe.modules.retrieval.RetrievalModuleContainer;
import de.ellpeck.prettypipes.pipe.modules.retrieval.RetrievalModuleGui;
import de.ellpeck.prettypipes.pipe.modules.retrieval.RetrievalModuleItem;
import de.ellpeck.prettypipes.pipe.modules.stacksize.StackSizeModuleContainer;
import de.ellpeck.prettypipes.pipe.modules.stacksize.StackSizeModuleGui;
import de.ellpeck.prettypipes.pipe.modules.stacksize.StackSizeModuleItem;
import de.ellpeck.prettypipes.pressurizer.PressurizerBlock;
import de.ellpeck.prettypipes.pressurizer.PressurizerBlockEntity;
import de.ellpeck.prettypipes.pressurizer.PressurizerContainer;
import de.ellpeck.prettypipes.pressurizer.PressurizerGui;
import de.ellpeck.prettypipes.terminal.CraftingTerminalBlock;
import de.ellpeck.prettypipes.terminal.CraftingTerminalBlockEntity;
import de.ellpeck.prettypipes.terminal.ItemTerminalBlock;
import de.ellpeck.prettypipes.terminal.ItemTerminalBlockEntity;
import de.ellpeck.prettypipes.terminal.containers.CraftingTerminalContainer;
import de.ellpeck.prettypipes.terminal.containers.CraftingTerminalGui;
import de.ellpeck.prettypipes.terminal.containers.ItemTerminalContainer;
import de.ellpeck.prettypipes.terminal.containers.ItemTerminalGui;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Locale;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber(bus = Bus.MOD)
public final class Registry {

    public static final CreativeModeTab TAB = new CreativeModeTab(PrettyPipes.ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registry.wrenchItem);
        }
    };

    public static Capability<PipeNetwork> pipeNetworkCapability = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static Capability<IPipeConnectable> pipeConnectableCapability = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static Item wrenchItem;
    public static Item pipeFrameItem;

    public static Block pipeBlock;
    public static BlockEntityType<PipeBlockEntity> pipeBlockEntity;
    public static MenuType<MainPipeContainer> pipeContainer;

    public static Block itemTerminalBlock;
    public static BlockEntityType<ItemTerminalBlockEntity> itemTerminalBlockEntity;
    public static MenuType<ItemTerminalContainer> itemTerminalContainer;

    public static Block craftingTerminalBlock;
    public static BlockEntityType<CraftingTerminalBlockEntity> craftingTerminalBlockEntity;
    public static MenuType<CraftingTerminalContainer> craftingTerminalContainer;

    public static EntityType<PipeFrameEntity> pipeFrameEntity;

    public static Block pressurizerBlock;
    public static BlockEntityType<PressurizerBlockEntity> pressurizerBlockEntity;
    public static MenuType<PressurizerContainer> pressurizerContainer;

    public static MenuType<ExtractionModuleContainer> extractionModuleContainer;
    public static MenuType<FilterModuleContainer> filterModuleContainer;
    public static MenuType<RetrievalModuleContainer> retrievalModuleContainer;
    public static MenuType<StackSizeModuleContainer> stackSizeModuleContainer;
    public static MenuType<FilterIncreaseModuleContainer> filterIncreaseModuleContainer;
    public static MenuType<CraftingModuleContainer> craftingModuleContainer;
    public static MenuType<FilterModifierModuleContainer> filterModifierModuleContainer;

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS, h -> {
            h.register(new ResourceLocation(PrettyPipes.ID, "pipe"), Registry.pipeBlock = new PipeBlock());
            h.register(new ResourceLocation(PrettyPipes.ID, "item_terminal"), Registry.itemTerminalBlock = new ItemTerminalBlock());
            h.register(new ResourceLocation(PrettyPipes.ID, "crafting_terminal"), Registry.craftingTerminalBlock = new CraftingTerminalBlock());
            h.register(new ResourceLocation(PrettyPipes.ID, "pressurizer"), Registry.pressurizerBlock = new PressurizerBlock());
        });

        event.register(ForgeRegistries.Keys.ITEMS, h -> {
            h.register(new ResourceLocation(PrettyPipes.ID, "wrench"), Registry.wrenchItem = new WrenchItem());
            h.register(new ResourceLocation(PrettyPipes.ID, "blank_module"), new Item(new Item.Properties().tab(Registry.TAB)));
            h.register(new ResourceLocation(PrettyPipes.ID, "pipe_frame"), Registry.pipeFrameItem = new PipeFrameItem());
            h.register(new ResourceLocation(PrettyPipes.ID, "stack_size_module"), new StackSizeModuleItem());
            h.register(new ResourceLocation(PrettyPipes.ID, "redstone_module"), new RedstoneModuleItem());
            h.register(new ResourceLocation(PrettyPipes.ID, "filter_increase_modifier"), new FilterIncreaseModuleItem());

            Registry.registerTieredModule(h, "extraction_module", ExtractionModuleItem::new);
            Registry.registerTieredModule(h, "filter_module", FilterModuleItem::new);
            Registry.registerTieredModule(h, "speed_module", SpeedModuleItem::new);
            Registry.registerTieredModule(h, "low_priority_module", LowPriorityModuleItem::new);
            Registry.registerTieredModule(h, "high_priority_module", HighPriorityModuleItem::new);
            Registry.registerTieredModule(h, "retrieval_module", RetrievalModuleItem::new);
            Registry.registerTieredModule(h, "crafting_module", CraftingModuleItem::new);

            for (var type : ItemEquality.Type.values()) {
                var name = type.name().toLowerCase(Locale.ROOT) + "_filter_modifier";
                h.register(new ResourceLocation(PrettyPipes.ID, name), new FilterModifierModuleItem(name, type));
            }
            for (var type : SortingModuleItem.Type.values()) {
                var name = type.name().toLowerCase(Locale.ROOT) + "_sorting_modifier";
                h.register(new ResourceLocation(PrettyPipes.ID, name), new SortingModuleItem(name, type));
            }

            ForgeRegistries.BLOCKS.getEntries().stream()
                    .filter(b -> b.getKey().location().getNamespace().equals(PrettyPipes.ID))
                    .forEach(b -> h.register(b.getKey().location(), new BlockItem(b.getValue(), new Item.Properties().tab(Registry.TAB))));
        });

        event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, h -> {
            h.register(new ResourceLocation(PrettyPipes.ID, "pipe"), Registry.pipeBlockEntity = BlockEntityType.Builder.of(PipeBlockEntity::new, Registry.pipeBlock).build(null));
            h.register(new ResourceLocation(PrettyPipes.ID, "item_terminal"), Registry.itemTerminalBlockEntity = BlockEntityType.Builder.of(ItemTerminalBlockEntity::new, Registry.itemTerminalBlock).build(null));
            h.register(new ResourceLocation(PrettyPipes.ID, "crafting_terminal"), Registry.craftingTerminalBlockEntity = BlockEntityType.Builder.of(CraftingTerminalBlockEntity::new, Registry.craftingTerminalBlock).build(null));
            h.register(new ResourceLocation(PrettyPipes.ID, "pressurizer"), Registry.pressurizerBlockEntity = BlockEntityType.Builder.of(PressurizerBlockEntity::new, Registry.pressurizerBlock).build(null));
        });

        event.register(ForgeRegistries.Keys.ENTITY_TYPES, h ->
                h.register(new ResourceLocation(PrettyPipes.ID, "pipe_frame"), Registry.pipeFrameEntity = EntityType.Builder.<PipeFrameEntity>of(PipeFrameEntity::new, MobCategory.MISC).build("pipe_frame")));

        event.register(ForgeRegistries.Keys.CONTAINER_TYPES, h -> {
            h.register(new ResourceLocation(PrettyPipes.ID, "pipe"), Registry.pipeContainer = IForgeMenuType.create((windowId, inv, data) -> new MainPipeContainer(Registry.pipeContainer, windowId, inv.player, data.readBlockPos())));
            h.register(new ResourceLocation(PrettyPipes.ID, "item_terminal"), Registry.itemTerminalContainer = IForgeMenuType.create((windowId, inv, data) -> new ItemTerminalContainer(Registry.itemTerminalContainer, windowId, inv.player, data.readBlockPos())));
            h.register(new ResourceLocation(PrettyPipes.ID, "crafting_terminal"), Registry.craftingTerminalContainer = IForgeMenuType.create((windowId, inv, data) -> new CraftingTerminalContainer(Registry.craftingTerminalContainer, windowId, inv.player, data.readBlockPos())));
            h.register(new ResourceLocation(PrettyPipes.ID, "pressurizer"), Registry.pressurizerContainer = IForgeMenuType.create((windowId, inv, data) -> new PressurizerContainer(Registry.pressurizerContainer, windowId, inv.player, data.readBlockPos())));

            Registry.extractionModuleContainer = Registry.registerPipeContainer(h, "extraction_module");
            Registry.filterModuleContainer = Registry.registerPipeContainer(h, "filter_module");
            Registry.retrievalModuleContainer = Registry.registerPipeContainer(h, "retrieval_module");
            Registry.stackSizeModuleContainer = Registry.registerPipeContainer(h, "stack_size_module");
            Registry.filterIncreaseModuleContainer = Registry.registerPipeContainer(h, "filter_increase_module");
            Registry.craftingModuleContainer = Registry.registerPipeContainer(h, "crafting_module");
            Registry.filterModifierModuleContainer = Registry.registerPipeContainer(h, "filter_modifier_module");
        });
    }

    private static <T extends AbstractPipeContainer<?>> MenuType<T> registerPipeContainer(RegisterEvent.RegisterHelper<MenuType<?>> helper, String name) {
        var type = (MenuType<T>) IForgeMenuType.create((windowId, inv, data) -> {
            var tile = Utility.getBlockEntity(PipeBlockEntity.class, inv.player.level, data.readBlockPos());
            var moduleIndex = data.readInt();
            var moduleStack = tile.modules.getStackInSlot(moduleIndex);
            return ((IModule) moduleStack.getItem()).getContainer(moduleStack, tile, windowId, inv, inv.player, moduleIndex);
        });
        helper.register(new ResourceLocation(PrettyPipes.ID, name), type);
        return type;
    }

    private static void registerTieredModule(RegisterEvent.RegisterHelper<Item> helper, String name, BiFunction<String, ModuleTier, ModuleItem> item) {
        for (var tier : ModuleTier.values())
            helper.register(new ResourceLocation(PrettyPipes.ID, tier.name().toLowerCase(Locale.ROOT) + "_" + name), item.apply(name, tier));
    }

    public static void setup(FMLCommonSetupEvent event) {
        PacketHandler.setup();
    }

    public static final class Client {

        public static void setup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(Registry.pipeBlock, RenderType.cutout());
            BlockEntityRenderers.register(Registry.pipeBlockEntity, PipeRenderer::new);
            EntityRenderers.register(Registry.pipeFrameEntity, PipeFrameRenderer::new);

            MenuScreens.register(Registry.pipeContainer, MainPipeGui::new);
            MenuScreens.register(Registry.itemTerminalContainer, ItemTerminalGui::new);
            MenuScreens.register(Registry.pressurizerContainer, PressurizerGui::new);
            MenuScreens.register(Registry.craftingTerminalContainer, CraftingTerminalGui::new);
            MenuScreens.register(Registry.extractionModuleContainer, ExtractionModuleGui::new);
            MenuScreens.register(Registry.filterModuleContainer, FilterModuleGui::new);
            MenuScreens.register(Registry.retrievalModuleContainer, RetrievalModuleGui::new);
            MenuScreens.register(Registry.stackSizeModuleContainer, StackSizeModuleGui::new);
            MenuScreens.register(Registry.filterIncreaseModuleContainer, FilterIncreaseModuleGui::new);
            MenuScreens.register(Registry.craftingModuleContainer, CraftingModuleGui::new);
            MenuScreens.register(Registry.filterModifierModuleContainer, FilterModifierModuleGui::new);
        }
    }
}
