package semantics;

public enum Type {
    CASH,
    SLICE,
    DOSSIER,
    ASSOCIATE,
    MARK,
    CREW,
    VOID,
    ERROR;

    public static Type fromTflString(String tfl) {
        if (tfl == null) return ERROR;
        return switch (tfl) {
            case "cash" -> CASH;
            case "slice" -> SLICE;
            case "dossier" -> DOSSIER;
            case "associate" -> ASSOCIATE;
            case "mark" -> MARK;
            case "crew" -> CREW;
            case "forgetAboutIt" -> VOID;
            default -> ERROR;
        };
    }
}
