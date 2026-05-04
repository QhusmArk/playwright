package com.example.playwright.pageObjects;

import com.example.helpers.StatusAssesser;
import com.example.playwright.components.parts.Icon;
import com.example.playwright.helpers.enums.ColourSchema;
import com.example.playwright.helpers.enums.IconType;

import static com.example.playwright.helpers.enums.IconType.PLUS;

// todo: Kan jag skapa en metod som fördelar icon+status och icontype?
// Oavsett vad borde jag väl kunna skicka <i> till egen metod så att jag direkt sätter IconType?
// Ev att jag kan skicka in <i> och sen kan jag stega mig upp i ancestors tills jag hittar det jag behöver, typ bg- eller clickable?


public class IconPO extends CommonPO {

    public Icon getIcon2(final String iconPath) {
        String tagName = actions().getTagName(iconPath);
        if (!"i".equals(tagName)) {
            throw new IllegalArgumentException("This method only accept <i> as tag for last element in path.");
        }

        Icon icon = new Icon();

        IconType iconType = getIconType(iconPath);
        icon.setType(iconType);

        ColourSchema iconColor = getElementColourByCss(iconPath);
        System.out.println("iconColor: " + iconColor);
        icon.setColour(iconColor);

        String color = actions().getComputedStyle(iconPath, "color");
        System.out.println("color: " + color);

        return icon;
    }

    // High-level entry point: analyze DOM -> choose scenario -> build icon.
    public Icon getIcon(final String parentPath) {
        System.out.println("******************* completeGetIcon start *******************");
        System.out.println("parentPath: " + parentPath);

        String tagName = actions().getTagName(parentPath);
        if ("i".equals(tagName)) {
            throw new IllegalArgumentException("This method do not accept <i> as tag for last element in path.");
        }

        IconContext context = buildIconContext(parentPath, tagName);
        IconScenario scenario = determineScenario(context);

        return buildIconForScenario(context, scenario);
    }

    /**
     * Holds all DOM facts used to determine and build the icon.
     */
    private static class IconContext {
        String parentPath;
        String tagName;
        String iconDescendantXpath;

        boolean isRunningReport;
        boolean isButtonOrA;
        boolean isSortingIcon;
        boolean isTableIcon;
        boolean isDiv;

        boolean hasMonIcon;
        boolean isOnCheckboxDiv;
        boolean hasIChild;
        boolean hasAChild;
        boolean hasDivChild;
        boolean hasButtonChild;

        int divChildrenCount;
        boolean childDivHasCheckbox;

        // Derived composite conditions
        boolean isTableIconWithBannerSibling;
        boolean isIconWithMultipleDivSiblings;
        boolean isSimpleIcon;

        boolean divElementExist; // for hidden icon case
    }

    /**
     * Populate all fields needed to determine scenario and build icon.
     */
    private IconContext buildIconContext(String parentPath, String tagName) {
        IconContext ctx = new IconContext();
        ctx.parentPath = parentPath;
        ctx.tagName = tagName;
        ctx.iconDescendantXpath = parentPath + " //i";

        ctx.isRunningReport = actions().elementExistAndVisible(
                parentPath + " //*[contains(@class,'spinner')]",
                false,
                0
        );

        ctx.isButtonOrA = "button".equals(tagName) || "a".equals(tagName);
        ctx.isSortingIcon = "th".equals(tagName);
        ctx.isTableIcon = "td".equals(tagName);
        ctx.isDiv = "div".equals(tagName);

        ctx.hasMonIcon = actions().elementExistAndVisible(parentPath + "/i[@data-qa-id='mon_status']", false, 0);
        ctx.isOnCheckboxDiv = actions().elementExistAndVisible(parentPath + "[@role='checkbox']", false, 0);
        ctx.hasIChild = actions().elementExistAndVisible(parentPath + "/i", false, 0);
        ctx.hasAChild = actions().elementExistAndVisible(parentPath + "/a", false, 0);
        ctx.hasDivChild = actions().elementExistAndVisible(parentPath + "/child::div", false, 0);
        ctx.hasButtonChild = actions().elementExistAndVisible(parentPath + "/button", false, 0);

        ctx.divChildrenCount = ctx.hasDivChild
                ? actions().countHowManyElements(parentPath)
                : 0;

        ctx.childDivHasCheckbox = ctx.hasDivChild
                && actions().elementExistAndVisible(parentPath + "/div[@role='checkbox']", false, 0);

        ctx.isTableIconWithBannerSibling = ctx.isTableIcon && ctx.hasIChild && ctx.hasDivChild;
        ctx.isIconWithMultipleDivSiblings = ctx.isDiv && ctx.hasIChild && ctx.hasDivChild;
        ctx.isSimpleIcon = (ctx.hasIChild && !ctx.hasDivChild) || ctx.hasMonIcon;

        ctx.divElementExist = actions().elementExistAndVisible(parentPath, false, 0);

        return ctx;
    }

    private enum IconScenario {
        SPINNER,
        SORTING_ICON_TH,
        DIV_WITH_A_CHILD,
        BUTTON_CHILD_NO_I_NO_DIV,
        ICON_INSIDE_BUTTON_OR_A,
        CHECKBOX_SELF_DIV,
        CHECKBOX_CHILD_DIV,
        TABLE_ICON_WITH_BANNER_SIBLING,
        SIMPLE_ICON_OR_MON_ICON,
        ICON_WITH_MULTIPLE_DIV_SIBLINGS,
        NESTED_ICON_DIV_CHILDREN_3,
        HIDDEN_ICON_IN_DIV
    }

    /**
     * Pure decision tree. Easy to tweak when you discover new icon structures.
     */
    private IconScenario determineScenario(IconContext ctx) {

        if (ctx.isRunningReport) {
            return IconScenario.SPINNER;
        }

        if (ctx.isSortingIcon) {
            return IconScenario.SORTING_ICON_TH;
        }

        if (ctx.hasAChild) {
            return IconScenario.DIV_WITH_A_CHILD;
        }

        if (!ctx.hasIChild && !ctx.hasDivChild && ctx.hasButtonChild) {
            return IconScenario.BUTTON_CHILD_NO_I_NO_DIV;
        }

        if (ctx.isButtonOrA) {
            return IconScenario.ICON_INSIDE_BUTTON_OR_A;
        }

        if (ctx.isOnCheckboxDiv) {
            return IconScenario.CHECKBOX_SELF_DIV;
        }

        if (!ctx.hasIChild && ctx.childDivHasCheckbox) {
            return IconScenario.CHECKBOX_CHILD_DIV;
        }

        if (ctx.isTableIconWithBannerSibling) {
            return IconScenario.TABLE_ICON_WITH_BANNER_SIBLING;
        }

        if (ctx.isSimpleIcon) {
            return IconScenario.SIMPLE_ICON_OR_MON_ICON;
        }

        if (ctx.isIconWithMultipleDivSiblings) {
            return IconScenario.ICON_WITH_MULTIPLE_DIV_SIBLINGS;
        }

        if (ctx.divChildrenCount == 3) {
            return IconScenario.NESTED_ICON_DIV_CHILDREN_3;
        }

        if (ctx.divElementExist) {
            return IconScenario.HIDDEN_ICON_IN_DIV;
        }

        throw new IllegalStateException("Could not map icon in completeGetIcon()");
    }

    /**
     * Map scenario to concrete icon-building logic.
     */
    private Icon buildIconForScenario(IconContext ctx, IconScenario scenario) {
        return switch (scenario) {
            case SPINNER -> buildSpinnerIcon(ctx);
            case SORTING_ICON_TH -> buildSortingIcon(ctx);
            case DIV_WITH_A_CHILD -> buildDivWithAChildIcon(ctx);
            case BUTTON_CHILD_NO_I_NO_DIV -> buildButtonChildNoIIcon(ctx);
            case ICON_INSIDE_BUTTON_OR_A -> buildIconInsideButtonOrA(ctx);
            case CHECKBOX_SELF_DIV -> buildCheckboxSelfDivIcon(ctx);
            case CHECKBOX_CHILD_DIV -> buildCheckboxChildDivIcon(ctx);
            case TABLE_ICON_WITH_BANNER_SIBLING -> buildTableIconWithBannerSibling(ctx);
            case SIMPLE_ICON_OR_MON_ICON -> buildSimpleOrMonIcon(ctx);
            case ICON_WITH_MULTIPLE_DIV_SIBLINGS -> buildIconWithMultipleDivSiblings(ctx);
            case NESTED_ICON_DIV_CHILDREN_3 -> buildNestedIconDivChildren3(ctx);
            case HIDDEN_ICON_IN_DIV -> buildHiddenIcon(ctx);
        };
    }

// --- Scenario builders ---------------------------------------------------

    private Icon buildSpinnerIcon(IconContext ctx) {
        Icon icon = new Icon();

        String spinnerPath = ctx.parentPath + " //*[contains(@class,'spinner')]";

        IconType iconType = getIconType(spinnerPath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(spinnerPath);
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(spinnerPath);
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildSortingIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.parentPath + "/i");
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath);
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.parentPath + " //i");
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildDivWithAChildIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.iconDescendantXpath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath + "/a");
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.iconDescendantXpath);
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildButtonChildNoIIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.iconDescendantXpath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath + "/button");
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.iconDescendantXpath);
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildIconInsideButtonOrA(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.iconDescendantXpath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath);
//        icon.setStatus(status);

        if (iconType.equals(PLUS)) {
            ColourSchema iconClassNameColour = getElementBackgroundColourByClassName(ctx.iconDescendantXpath);
            icon.setColour(iconClassNameColour);
        } else {
            ColourSchema iconColour = getElementColourByCss(ctx.iconDescendantXpath);
            icon.setColour(iconColour);
        }

        return icon;
    }

    private Icon buildCheckboxSelfDivIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getCheckboxIcon(ctx.parentPath);
        icon.setType(iconType);

        boolean hasAriaDisabledAttribute = actions().elementExistAndVisible(ctx.parentPath + "[@aria-disabled]", false, 0);
//        StatusAssesser.Status status = hasAriaDisabledAttribute
//                ? DISABLED
//                : CLICKABLE;
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.parentPath + "/div");
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildCheckboxChildDivIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getCheckboxIcon(ctx.parentPath + "/div");
        icon.setType(iconType);

//        icon.setStatus(CLICKABLE);

        ColourSchema iconColour = getElementColourByClassName(ctx.parentPath + "/div/div");
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildTableIconWithBannerSibling(IconContext ctx) {
        Icon icon = new Icon();

        /*
        If the <td> in //table/tbody[@class='q-virtual-scroll__content']/tr[1]/td[2]/i[1]/parent::td
        has multiple <i> then we need to 'step up' to the numbered <i>. In this case the /i[1].
         */
        if (ctx.parentPath.contains("/parent::td")) {
            ctx.parentPath = ctx.parentPath.replace("/parent::td", "");

            IconType iconType = getIconType(ctx.parentPath);
            icon.setType(iconType);

            ColourSchema iconColour = getElementColourByCss(ctx.parentPath);
            icon.setColour(iconColour);

        } else {

            IconType iconType = getIconType(ctx.parentPath + "/i");
            icon.setType(iconType);

            ColourSchema iconColour = getElementColourByCss(ctx.parentPath + "/i");
            icon.setColour(iconColour);

        }
        return icon;
    }

    private Icon buildSimpleOrMonIcon(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.parentPath + "/i");
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath);
//        icon.setStatus(status);

        String iconClassName = actions().findOneElementsAttribute(ctx.parentPath + "/i", "class");

        ColourSchema iconColour = (!iconClassName.contains("text-"))
                ? getElementColourByCss(ctx.parentPath + "/i")
                : getElementColourByClassName(ctx.parentPath + "/i");

        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildIconWithMultipleDivSiblings(IconContext ctx) {
        // Same as buildSimpleOrMonIcon, but separated if you later want special handling
        return buildSimpleOrMonIcon(ctx);
    }

    private Icon buildNestedIconDivChildren3(IconContext ctx) {
        Icon icon = new Icon();

        IconType iconType = getIconType(ctx.iconDescendantXpath);
        icon.setType(iconType);

//        StatusAssesser.Status status = getElementStatusByClassName(ctx.parentPath);
//        icon.setStatus(status);

        ColourSchema iconColour = getElementColourByCss(ctx.iconDescendantXpath);
        icon.setColour(iconColour);

        return icon;
    }

    private Icon buildHiddenIcon(IconContext ctx) {
        Icon icon = new Icon();

        String divElement = actions().findOneElementsAttribute(ctx.parentPath, "outerHTML");

        boolean divContainsIcon = divElement.contains("<i class=\"q-icon notranslate material-icons\" aria-hidden=\"true\" role=\"img\">");
        boolean iIsVisible = actions().elementExistAndVisible(ctx.parentPath + " //i", false, 0);

        if (divContainsIcon && !iIsVisible) {
            IconType iconType = null;
            if (divElement.contains("settings")) {
                iconType = IconType.fromClassName("settings");
            } else if (divElement.contains("arrow_forward")) {
                iconType = IconType.fromClassName("arrow_forward");
            }
            icon.setType(iconType);
        }

//        icon.setStatus(INVISIBLE);

        return icon;
    }

    public ColourSchema getElementBackgroundColourByClassName(String elementPath) {
        return getElementBackgroundColourByClassName(elementPath);
    }

    public StatusAssesser.Status getElementStatusByClassName(String elementPath) {
            return getElementStatusByClassName(elementPath);
    }

    public ColourSchema getElementColourByCss(String elementPath) {
        return getElementColourByCss(elementPath);
    }

    public ColourSchema getElementColourByClassName(String elementPath) {
        return getElementColourByClassName(elementPath);
    }

    /**
     * There are two types of icons. Those with identifier in class name ('q-icon text-primary icon-projects')
     * and those with identifier in web element text ('q-icon notranslate material-icons q-mr-md q-mt-sm q-pl-sm text-infra-secondary').
     * If we find 'material-icons' in class attribute, then we need to get the icon text. That is why this method is here, and not in IconType
     */
    private IconType getIconType(String iconPath) {
        String className = actions().findOneElementsAttribute(iconPath, "class");

        // A sorting arrow has always text 'arrow_upward'. The parent has information about it pointing down (desc).
        if (className.contains("sort-icon")) {  // 'q-icon notranslate material-icons q-table__sort-icon q-table__sort-icon--left'
            String parentClassName = actions().findOneElementsAttribute(iconPath + "/parent::th", "class");

            String sortingIconIdentifier = (parentClassName.contains("sort-desc"))
                    ? "sort-icon-descending"    // om 'text-left sortable sorted sort-desc'
                    : "sort-icon-ascending";    // om 'text-left sortable sorted'

            return IconType.fromClassName(sortingIconIdentifier);

        } else {

            if (className.contains("q-expansion")) {
                className = (className.contains("rotated"))
                        ? "keyboard_arrow_down_expanded"
                        : "keyboard_arrow_down_collapsed";
            }

            String iconIdentifier = (className.contains("material-icons"))
                    ? actions().findOneElementsText(iconPath)
                    : IconType.extractIconTypeFromIconClassName(className);

            return IconType.fromClassName(iconIdentifier);
        }

    }

    /**
     * false, true, mixed
     */
    private IconType getCheckboxIcon(String checkboxPath) {
        String checkboxIdentifier = "checkbox_" + actions().findOneElementsAttribute(checkboxPath, "aria-checked");
        return IconType.fromClassName(checkboxIdentifier);
    }

}
