package com.zzhalex233.bqjei.integration.jei;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.GuiQuest;
import betterquesting.questing.rewards.RewardChoice;
import betterquesting.questing.rewards.RewardItem;
import betterquesting.questing.tasks.TaskCheckbox;
import betterquesting.questing.tasks.TaskCrafting;
import betterquesting.questing.tasks.TaskRetrieval;
import com.zzhalex233.bqjei.BetterQuestingJEI;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestWrapper implements IRecipeWrapper, ITooltipCallback<ItemStack> {
    public final int questId;
    public final IQuest quest;
    public final String name;
    public final boolean hidden;
    public final List<List<ItemStack>> input;
    public final List<List<ItemStack>> output;

    private final List<SlotData> inputInfo;
    private final List<SlotData> outputInfo;

    private QuestWrapper(int questId, IQuest quest, boolean hidden, List<SlotData> inputs, List<SlotData> outputs) {
        this.questId = questId;
        this.quest = quest;
        this.hidden = hidden;
        this.name = QuestTranslation.translate(quest.getProperty(NativeProps.NAME));
        this.inputInfo = prepareSlots(inputs);
        this.outputInfo = prepareSlots(outputs);
        this.input = toIngredientLists(inputInfo);
        this.output = toIngredientLists(outputInfo);
    }

    public static QuestWrapper create(int questId, IQuest quest, boolean hidden) {
        List<SlotData> inputs = new ArrayList<>();
        List<SlotData> outputs = new ArrayList<>();

        for (DBEntry<ITask> entry : quest.getTasks().getEntries()) {
            ITask task = entry.getValue();

            if (task instanceof TaskRetrieval) {
                addRetrievalInputs(inputs, (TaskRetrieval) task);
            } else if (task instanceof TaskCrafting) {
                addCraftingInputs(inputs, (TaskCrafting) task);
            } else if (task instanceof TaskCheckbox) {
                addCheckboxInput(inputs, (TaskCheckbox) task);
            }
        }

        for (DBEntry<IReward> entry : quest.getRewards().getEntries()) {
            IReward reward = entry.getValue();

            if (reward instanceof RewardItem) {
                addItemRewards(outputs, (RewardItem) reward);
            } else if (reward instanceof RewardChoice) {
                addChoiceRewards(outputs, (RewardChoice) reward);
            }
        }

        if (inputs.isEmpty() && outputs.isEmpty()) {
            return null;
        }

        return new QuestWrapper(questId, quest, hidden, inputs, outputs);
    }

    public int getInputSlotCount() {
        return inputInfo.size();
    }

    public int getOutputSlotCount() {
        return outputInfo.size();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, input);
        ingredients.setOutputLists(VanillaTypes.ITEM, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        FontRenderer font = minecraft.fontRenderer;
        String title = trimToWidth(font, name, recipeWidth - 8);
        int width = font.getStringWidth(title);
        int x = (recipeWidth - width) / 2;
        int y = 3;
        boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + font.FONT_HEIGHT;
        int color = hovered ? 0xFFA87A5E : (hidden ? 0xFF6B4F7A : 0xFF3F2E23);

        font.drawString(TextFormatting.UNDERLINE + title, x, y, color);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseY < 0 || mouseY >= 18) {
            return Collections.emptyList();
        }

        List<String> tooltip = new ArrayList<>();
        tooltip.add(name);
        tooltip.add(TextFormatting.GRAY + I18n.format("bqjei.open_quest"));
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("bqjei.quest_id", questId));

        if (hidden) {
            tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("bqjei.hidden_quest"));
        }

        return tooltip;
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        if (mouseY >= 0 && mouseY < 18 && minecraft.player != null) {
            minecraft.displayGuiScreen(new GuiQuest(minecraft.currentScreen, questId));
            return true;
        }

        return false;
    }

    @Override
    public void onTooltip(int slot, boolean inputSlot, ItemStack ingredient, List<String> tooltip) {
        SlotData data = inputSlot ? getSlotData(inputInfo, slot) : getSlotData(outputInfo, slot - getInputSlotCount());
        if (data == null || data.stacks.isEmpty()) {
            return;
        }

        tooltip.add(TextFormatting.GRAY + I18n.format(data.roleKey));
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("bqjei.amount", data.amount));

        if (!data.oreDict.isEmpty()) {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format("bqjei.ore_dict", data.oreDict));
        }

        for (String key : data.noteKeys) {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format(key));
        }

        if (hidden) {
            tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("bqjei.hidden_quest"));
        }
    }

    private static void addRetrievalInputs(List<SlotData> inputs, TaskRetrieval task) {
        List<String> notes = new ArrayList<>();
        notes.add(task.consume ? "bqjei.task.retrieval.consume" : "bqjei.task.retrieval.detect");

        if (!task.partialMatch) {
            notes.add("bqjei.match.exact");
        }

        if (task.ignoreNBT) {
            notes.add("bqjei.match.ignore_nbt");
        }

        if (task.optional) {
            notes.add("bqjei.optional");
        }

        for (BigItemStack stack : task.requiredItems) {
            addBigStack(inputs, stack, "bqjei.task.retrieval", notes);
        }
    }

    private static void addCraftingInputs(List<SlotData> inputs, TaskCrafting task) {
        List<String> notes = new ArrayList<>();

        if (!task.partialMatch) {
            notes.add("bqjei.match.exact");
        }

        if (task.ignoreNBT) {
            notes.add("bqjei.match.ignore_nbt");
        }

        if (task.optional) {
            notes.add("bqjei.optional");
        }

        for (BigItemStack stack : task.requiredItems) {
            addBigStack(inputs, stack, "bqjei.task.crafting", notes);
        }
    }

    private static void addCheckboxInput(List<SlotData> inputs, TaskCheckbox task) {
        List<String> notes = new ArrayList<>();

        if (task.optional) {
            notes.add("bqjei.optional");
        }

        inputs.add(new SlotData(
                Collections.singletonList(new ItemStack(BetterQuestingJEI.CHECKBOX)),
                "bqjei.task.checkbox",
                1,
                "",
                notes
        ));
    }

    private static void addItemRewards(List<SlotData> outputs, RewardItem reward) {
        for (BigItemStack stack : reward.items) {
            addBigStack(outputs, stack, "bqjei.reward.item", Collections.emptyList());
        }
    }

    private static void addChoiceRewards(List<SlotData> outputs, RewardChoice reward) {
        List<String> notes = Collections.singletonList("bqjei.reward.choice.note");

        for (BigItemStack stack : reward.choices) {
            addBigStack(outputs, stack, "bqjei.reward.choice", notes);
        }
    }

    private static void addBigStack(List<SlotData> items, BigItemStack stack, String roleKey, List<String> noteKeys) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }

        List<ItemStack> stacks = getDisplayStacks(stack);
        if (!stacks.isEmpty()) {
            items.add(new SlotData(stacks, roleKey, stack.stackSize, stack.hasOreDict() ? stack.getOreDict() : "", noteKeys));
        }
    }

    private static List<ItemStack> getDisplayStacks(BigItemStack bigStack) {
        if (bigStack.hasOreDict()) {
            List<ItemStack> stacks = new ArrayList<>();

            for (ItemStack stack : bigStack.getOreIngredient().getMatchingStacks()) {
                if (stack != null && !stack.isEmpty()) {
                    stacks.add(copyWithDisplayCount(stack, bigStack.stackSize));
                }
            }

            if (!stacks.isEmpty()) {
                return stacks;
            }
        }

        ItemStack baseStack = bigStack.getBaseStack();
        if (baseStack == null || baseStack.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.singletonList(copyWithDisplayCount(baseStack, bigStack.stackSize));
    }

    private static ItemStack copyWithDisplayCount(ItemStack stack, int amount) {
        ItemStack copy = stack.copy();
        copy.setCount(Math.max(1, Math.min(amount, copy.getMaxStackSize())));
        return copy;
    }

    private static List<SlotData> prepareSlots(List<SlotData> slots) {
        int limit = Math.min(9, slots.size());
        if (limit <= 0) {
            return Collections.emptyList();
        }

        if (limit == 1) {
            List<SlotData> centered = new ArrayList<>(5);
            for (int i = 0; i < 4; i++) {
                centered.add(SlotData.EMPTY);
            }
            centered.add(slots.get(0));
            return centered;
        }

        return new ArrayList<>(slots.subList(0, limit));
    }

    private static List<List<ItemStack>> toIngredientLists(List<SlotData> slots) {
        List<List<ItemStack>> lists = new ArrayList<>(slots.size());

        for (SlotData slot : slots) {
            lists.add(slot.stacks);
        }

        return lists;
    }

    private static SlotData getSlotData(List<SlotData> slots, int index) {
        if (index < 0 || index >= slots.size()) {
            return null;
        }

        return slots.get(index);
    }

    private static String trimToWidth(FontRenderer font, String text, int maxWidth) {
        if (font.getStringWidth(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        int textWidth = Math.max(0, maxWidth - font.getStringWidth(ellipsis));
        return font.trimStringToWidth(text, textWidth) + ellipsis;
    }

    private static final class SlotData {
        private static final SlotData EMPTY = new SlotData(
                Collections.emptyList(),
                "",
                0,
                "",
                Collections.emptyList()
        );

        private final List<ItemStack> stacks;
        private final String roleKey;
        private final int amount;
        private final String oreDict;
        private final List<String> noteKeys;

        private SlotData(List<ItemStack> stacks, String roleKey, int amount, String oreDict, List<String> noteKeys) {
            this.stacks = Collections.unmodifiableList(new ArrayList<>(stacks));
            this.roleKey = roleKey;
            this.amount = amount;
            this.oreDict = oreDict == null ? "" : oreDict;
            this.noteKeys = Collections.unmodifiableList(new ArrayList<>(noteKeys));
        }
    }
}
