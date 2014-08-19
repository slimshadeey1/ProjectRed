package mrtjp.projectred.exploration;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.UVTranslation;
import codechicken.lib.vec.*;
import codechicken.microblock.Saw;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.projectred.ProjectRedExploration;
import mrtjp.projectred.core.ItemCraftingDamage;
import mrtjp.projectred.core.PRColors;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ItemGemSaw extends ItemCraftingDamage implements Saw
{
    EnumSpecialTool tool;

    protected ItemGemSaw(int par1, EnumSpecialTool tool)
    {
        super(par1);
        this.tool = tool;
        this.setUnlocalizedName("projectred.exploration." + tool.unlocal);
        this.setMaxDamage(tool.material.getMaxUses());
        this.setCreativeTab(ProjectRedExploration.tabExploration());
    }

    @Override
    public int getMaxCuttingStrength()
    {
        return getCuttingStrength(null);
    }

    @Override
    public int getCuttingStrength(ItemStack stack)
    {
        return tool.material.getHarvestLevel();
    }

    @Override
    public boolean hasContainerItem()
    {
        return true;
    }

    @Override
    public ItemStack getContainerItemStack(ItemStack stack)
    {
        if (stack.itemID == this.itemID)
        {
            stack.setItemDamage(stack.getItemDamage() + 1);
            return stack;
        }
        else
        {
            ItemStack newStack = new ItemStack(this);
            newStack.setItemDamage(newStack.getMaxDamage());
            return newStack;
        }
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack is)
    {
        return false;
    }

    @Override
    public void registerIcons(IconRegister reg)
    {
    }

    @SideOnly(Side.CLIENT)
    public static class GemSawItemRenderer implements IItemRenderer
    {

        Map<String, CCModel> models;
        CCModel handle;
        CCModel holder;
        CCModel blade;

        public static GemSawItemRenderer instance = new GemSawItemRenderer();

        public GemSawItemRenderer()
        {
            models = CCModel.parseObjModels(new ResourceLocation("microblock", "models/saw.obj"), 7, new SwapYZ());
            handle = models.get("Handle");
            holder = models.get("BladeSupport");
            blade = models.get("Blade");
        }

        @Override
        public boolean handleRenderType(ItemStack item, ItemRenderType type)
        {
            return true;
        }

        @Override
        public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
        {
            return true;
        }

        public int getColorForStack(ItemStack saw)
        {
            EnumToolMaterial m = ((ItemGemSaw) saw.getItem()).tool.material;

            if (m == EnumToolMaterial.WOOD)
                return PRColors.BROWN.rgb;
            if (m == EnumToolMaterial.STONE)
                return PRColors.LIGHT_GREY.rgb;
            if (m == EnumToolMaterial.IRON)
                return PRColors.WHITE.rgb;
            if (m == EnumToolMaterial.GOLD)
                return PRColors.YELLOW.rgb;
            if (m == ProjectRedExploration.toolMaterialRuby())
                return PRColors.RED.rgb;
            if (m == ProjectRedExploration.toolMaterialSapphire())
                return PRColors.BLUE.rgb;
            if (m == ProjectRedExploration.toolMaterialPeridot())
                return PRColors.GREEN.rgb;
            if (m == EnumToolMaterial.EMERALD)
                return PRColors.CYAN.rgb;
            return PRColors.BLACK.rgb;
        }

        @Override
        public void renderItem(ItemRenderType type, ItemStack item, Object... data)
        {
            TransformationList t;
            switch (type) {
            case INVENTORY:
                t = new TransformationList(new Scale(1.8), new Translation(0, 0, -0.6), new Rotation(-MathHelper.pi / 4, 1, 0, 0), new Rotation(MathHelper.pi * 3 / 4, 0, 1, 0));
                break;
            case ENTITY:
                t = new TransformationList(new Scale(1), new Translation(0, 0, -0.25), new Rotation(-MathHelper.pi / 4, 1, 0, 0));
                break;
            case EQUIPPED_FIRST_PERSON:
                t = new TransformationList(new Scale(1.5), new Rotation(-MathHelper.pi / 3, 1, 0, 0), new Rotation(MathHelper.pi * 3 / 4, 0, 1, 0), new Translation(0.5, 0.5, 0.5));
                break;
            case EQUIPPED:
                t = new TransformationList(new Scale(1.5), new Rotation(-MathHelper.pi / 5, 1, 0, 0), new Rotation(-MathHelper.pi * 3 / 4, 0, 1, 0), new Translation(0.75, 0.5, 0.75));
                break;
            default:
                return;
            }
            CCRenderState.reset();
            CCRenderState.useNormals(true);
            CCRenderState.pullLightmap();
            CCRenderState.changeTexture("microblock:textures/items/saw.png");
            CCRenderState.setColour(0xFFFFFFFF);
            CCRenderState.startDrawing(7);
            handle.render(t, null);
            holder.render(t, null);
            CCRenderState.draw();
            if (type != ItemRenderType.EQUIPPED_FIRST_PERSON)
                GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            CCRenderState.startDrawing(7);
            CCRenderState.setColourOpaque(getColorForStack(item));
            blade.render(t, new UVTranslation(0, (2 - 1) * 4 / 64D));
            CCRenderState.setColour(0xFFFFFFFF);
            CCRenderState.draw();
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (type != ItemRenderType.EQUIPPED_FIRST_PERSON)
                GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

}
