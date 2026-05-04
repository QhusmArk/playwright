package com.example.playwright.steps;

import com.example.helpers.Randomizer;
import com.example.playwright.components.aside.Aside;
import com.example.playwright.components.aside.asideItems.listItems.BlastItem;
import com.example.playwright.components.parts.Icon;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.ColourSchema;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.util.List;

import static com.example.playwright.helpers.enums.IconType.BLAST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlastGlue extends BaseGlue {

    @Then("I can copy a blast")
    public void iCanCopyABlast() {
        String templateBlastsName = context().getBlasts().getFirst().getBlastId();
        String newBlastsName = Randomizer.randomString(8);
        blastPO.copyBlast(templateBlastsName, newBlastsName);
        PlaywrightActions.sleep(2);

//        ag.thisCanBeFoundInAside("blast", newBlastsName);
        List<BlastItem> blasts = asidePO.getAside().getBlastItems();
        assertTrue(blasts.stream().anyMatch(blast -> blast.getName().contains(newBlastsName)));
    }

    @Then("I can create a blast")
    public void iCanCreateABlast() {
        String name = Randomizer.randomString(10);
        double latitude = 59.31296;
        double longitude = 18.08457;

        blastPO.createBlast(name, latitude, longitude);

//        ag.thisCanBeFoundInAside("blast", name);
        List<BlastItem> blasts = asidePO.getAside().getBlastItems();
        assertTrue(blasts.stream().anyMatch(blast -> blast.getName().contains(name)));
    }

    @Then("I can change the blasts name")
    public void iCanChangeTheBlastsName() {
        // Create a new name and replace the old name
        String newName = Randomizer.randomString(8);
        blastPO.changeBlastName(newName);
        PlaywrightActions.sleep(2);

//        ag.thisCanBeFoundInAside("blast", newName);
        List<BlastItem> blasts = asidePO.getAside().getBlastItems();
        assertTrue(blasts.stream().anyMatch(blast -> blast.getName().contains(newName)));
    }

    @Then("blast {string} can be found in aside")
    public void thisCanBeFoundInAside(String expectedText) {
        List<BlastItem> blasts = asidePO.getAside().getBlastItems();
        assertTrue(blasts.stream().anyMatch(blast -> blast.getName().contains(expectedText)));
    }

    @And("I can delete the blast")
    public void iCanDeleteTheBlast() {
        blastPO.deleteBlast();
        PlaywrightActions.sleep(2);

        // Get actual blasts
        List<BlastItem> blastsAfterChange = asidePO.getAside().getBlastItems();
        assertTrue(blastsAfterChange.isEmpty());
    }

    /**
     * A planned blast icon is light blue in COMPACT and MEDIUM.
     * A passed blast icon is primary colour in COMPACT but disabled in MEDIUM.
     */
    @Then("all blast icons has {string} color in {string}")
    public void allBlastsHasPrimaryColor(String expectedColour, String asideSize) {
        if ("COMPACT".equals(asideSize)) {
            List<BlastItem> asideItems = asidePO.getAside().getBlastItems();

            asideItems.forEach(blast -> {
                Icon blastIcon = blast.getLeftIcon();

                assertEquals(BLAST, blastIcon.getType());
                assertTrue(blastIcon.getColour().equals(ColourSchema.LIGHT_BLUE) || blastIcon.getColour().equals(ColourSchema.PRIMARY));
            });
        } else {
            Aside aside = asidePO.getAside();

            aside.getTable().getContent().forEach(blastRow -> {
                Icon blastIcon = blastRow.getRowLeftIcon();

                assertEquals(BLAST, blastIcon.getType());
                assertTrue(blastIcon.getColour().equals(ColourSchema.LIGHT_BLUE) || blastIcon.getColour().equals(ColourSchema.DISABLED));
            });
        }
    }
}
