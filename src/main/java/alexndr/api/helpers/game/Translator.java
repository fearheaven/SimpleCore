package alexndr.api.helpers.game;

import java.util.IllegalFormatException;
import java.util.Locale;

import alexndr.api.logger.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.util.text.translation.I18n;

/** 
 * Shamelessly cut & pasted from Mezz's mezz.jei.util.Translator, because a
 * wrapper around deprecated i8ln functions is badly needed.
 * @author Sinhika
 *
 */
@SuppressWarnings("deprecation")
public final class Translator {
	private Translator() {
	}

	public static String translateToLocal(String key) {
		if (I18n.canTranslate(key)) {
			return I18n.translateToLocal(key);
		} else {
			return I18n.translateToFallback(key);
		}
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		String s = translateToLocal(key);
		try {
			return String.format(s, format);
		} catch (IllegalFormatException e) {
			LogHelper.severe("Format error: " + s);
			LogHelper.verboseException(e);
			return "Format error: " + s;
		}
	}

	public static String toLowercaseWithLocale(String string) {

		return string.toLowerCase(getLocale());
	}

	private static Locale getLocale() {
		Minecraft minecraft = Minecraft.getMinecraft();
		if (minecraft != null) {
			LanguageManager languageManager = minecraft.getLanguageManager();
			if (languageManager != null) {
				Language currentLanguage = languageManager.getCurrentLanguage();
				if (currentLanguage != null) {
					return currentLanguage.getJavaLocale();
				}
			}
		}
		return Locale.getDefault();
	}

} // end class
