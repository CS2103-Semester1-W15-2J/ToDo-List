package tasknote.ui;

public abstract class GuiConstant {
    private GuiConstant() {
        // Prevent instantiation of GuiConstant
    }
    
    // TODO Check if package access is allowed
    public static final String COMMAND_ADD = "add";
    public static final String COMMAND_EDIT = "edit";
    public static final String COMMAND_RENAME = "rename";
    public static final String COMMAND_DONE = "done";
    public static final String COMMAND_DELETE = "delete";
    public static final String COMMAND_UNDO = "undo";
    public static final String COMMAND_SEARCH = "search";
    
    public static final String DEFAULT_COMMAND = COMMAND_ADD + " ";
    
    public static final String PROPERTY_BACKGROUND_COLOR = "-fx-background-color: %1$s;";
    public static final String PROPERTY_FONT_WEIGHT = "-fx-font-weight: %1$s;";
    public static final String PROPERTY_FONT_SIZE = "-fx-font-size: %1$dpx;";
    public static final String PROPERTY_TEXT_INNER_COLOR = "-fx-text-inner-color: %1$s;";
    public static final String PROPERTY_TEXT_FILL = "-fx-text-fill: %1$s;";
    
    public static final String COLOR_HEX_CODE_WHITE = "#ffffff";
    
    public static int PADDING_HORIZONTAL = 10;
    public static int PADDING_VERTICAL = 15;
    public static int SPACING_BETWEEN_COMPONENTS = 10;
}