# Third-party components

ZeniQuill is licensed under GPL-3.0. This file records third-party code,
assets and libraries used by the project and their licenses.

## Runtime dependencies (Maven)

| Component | License | Source |
|---|---|---|
| sora-editor (`io.github.Rosemoe.sora-editor:editor`, `:language-textmate`, `:oniguruma-native`) 0.23.6 | LGPL-2.1 | https://github.com/Rosemoe/sora-editor |

Used as binary dependencies; no source code vendored.

## Bundled assets (app/src/main/assets/textmate)

TextMate grammar files (`*.tmLanguage.json`, `language-configuration.json`)
and the `darcula.json` / `quietlight.json` editor themes are copied from
**Xed-Editor** (https://github.com/Xed-Editor/Xed-Editor, GPL-3.0), which
itself redistributes syntax definitions from the Visual Studio Code
language extensions (MIT).

- Grammar assets organization and theme JSONs: GPL-3.0 (Xed-Editor project)
- Original grammar definitions: MIT (Microsoft/vscode and respective authors)

Per-language provenance:

| Language | Origin |
|---|---|
| Python | vscode-python (MIT) via Xed-Editor |
| YAML | vscode-yaml (MIT) via Xed-Editor |
| C | vscode-cpp grammar (MIT) via Xed-Editor |
| Java | vscode-java (MIT) via Xed-Editor |
| HTML | vscode-html (MIT) via Xed-Editor |
| XML | vscode-xml (MIT) via Xed-Editor |
| Plain text | vscode-text (MIT) via Xed-Editor |
| darcula.json / quietlight.json | Xed-Editor (GPL-3.0) |

## Design references (no code copied)

- Xed-Editor (GPL-3.0) — editor layer and settings application patterns
- Visual-Code-Space (GPL-3.0) — Compose UI structure reference

ZeniQuill as a whole is distributed under GPL-3.0.
