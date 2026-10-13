package MiniCash.itemLoader.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {

    private static final String SEPARATOR = "==================================================";
    private static final String PREFIX = "[ItemLoaderAPI] ";

    public static void logSyntaxError(Logger logger, String filePath, String errorMessage) {
        logger.severe(SEPARATOR);
        logger.severe(PREFIX + "【YAML構文エラー】設定ファイルの構造が壊れています。");
        logger.severe(PREFIX + "ファイル場所: " + filePath);
        logger.severe(PREFIX + "原因: " + errorMessage);
        logger.severe(SEPARATOR);
    }

    public static void logConfigError(Logger logger, String filePath, String itemId, String message) {
        logger.warning(SEPARATOR);
        logger.warning(PREFIX + "【設定ミス】" + message);
        logger.warning(PREFIX + "ファイル場所: " + filePath);
        logger.warning(PREFIX + "対象アイテム: " + itemId);
        logger.severe(SEPARATOR);
    }

    public static void logSystemError(Logger logger, String filePath, String message, Throwable e) {
        logger.severe(SEPARATOR);
        logger.severe(PREFIX + "【システムエラー】予期せぬエラーが発生しました。");
        logger.severe(PREFIX + "ファイル場所: " + filePath);
        logger.severe(SEPARATOR);
        logger.log(Level.SEVERE, PREFIX + message, e);
    }


}
