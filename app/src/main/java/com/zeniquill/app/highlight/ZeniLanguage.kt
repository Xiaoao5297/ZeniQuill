package com.zeniquill.app.highlight

/**
 * TextMate grammar entries bundled under assets/textmate/.
 * Scope names mirror the scopeName field of each tmLanguage file.
 */
enum class ZeniLanguage(
    val id: String,
    val displayName: String,
    val scopeName: String,
    val grammarPath: String,
    val extensions: List<String>,
) {    PYTHON(
        "python", "Python", "source.python",
        "textmate/python/syntaxes/python.tmLanguage.json",
        listOf("py", "pyw"),
    ),
    YAML(
        "yaml", "YAML", "source.yaml",
        "textmate/yaml/syntaxes/yaml.tmLanguage.json",
        listOf("yml", "yaml"),
    ),
    C(
        "c", "C", "source.c",
        "textmate/c/syntaxes/c.tmLanguage.json",
        listOf("c", "h"),
    ),
    JAVA(
        "java", "Java", "source.java",
        "textmate/java/syntaxes/java.tmLanguage.json",
        listOf("java"),
    ),
    HTML(
        "html", "HTML", "text.html.basic",
        "textmate/html/syntaxes/html.tmLanguage.json",
        listOf("html", "htm"),
    ),
    XML(
        "xml", "XML", "text.xml",
        "textmate/xml/syntaxes/xml.tmLanguage.json",
        listOf("xml", "svg"),
    ),
    PLAIN(
        "text", "Plain Text", "text.plain",
        "textmate/text/syntaxes/text.tmLanguage.json",
        listOf("txt", "text", "log", "md"),
    );

    companion object {
        /** Detect language from a file name (extension based). */
        fun fromFileName(name: String?): ZeniLanguage {
            val ext = name?.substringAfterLast('.', "")?.lowercase() ?: ""
            if (ext.isEmpty()) return PLAIN
            return entries.firstOrNull { lang -> ext in lang.extensions } ?: PLAIN
        }
    }
}
