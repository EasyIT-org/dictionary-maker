#!/usr/bin/env python3
"""Convert project PDF sources into plain-text novels ready for dictionary generation."""

import argparse
import re
import string
import sys
from functools import lru_cache
from pathlib import Path

try:
    from pypdf import PdfReader
except ImportError:  # pragma: no cover - dependency guard
    sys.stderr.write(
        "Missing dependency: install pypdf (pip install pypdf) before running this script.\n"
    )
    sys.exit(1)


DEFAULT_PDF_DIR = Path("src/main/resources/pdfs")
DEFAULT_OUTPUT_DIR = Path("src/main/resources/novels")
CANDIDATE_LIGATURES: tuple[str, ...] = ("fi", "fl", "ff", "ffi", "ffl")
WORD_SPLIT_RE = re.compile(r"[-—/]")
PUNCT_CHARS = set(string.punctuation + "“”‘’—–…")
KNOWN_SUFFIXES: tuple[str, ...] = (
    "s", "es", "ed", "ing", "ly", "ness", "ment", "ments", "tion", "tions",
    "al", "ally", "er", "ers", "ist", "ists", "ful", "fully", "ous", "ously",
    "ance", "ances", "ence", "ences", "ality", "alities", "ation", "ations", "ative", "atives",
    "ivity", "ivities", "ization", "izations", "izer", "izers", "less", "lessly", "able",
    "ables", "ible", "ibles", "ship", "ships", "hood", "hoods", "nesses", "ity", "ities",
    "ify", "ifies", "ified", "ifying", "ic", "ical", "ically", "icism", "icist", "ician", "icians",
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Extract text from PDFs and store .txt files under the novels directory."
    )
    parser.add_argument(
        "--source",
        type=Path,
        default=DEFAULT_PDF_DIR,
        help="Directory that contains PDF files (default: %(default)s).",
    )
    parser.add_argument(
        "--destination",
        type=Path,
        default=DEFAULT_OUTPUT_DIR,
        help="Directory to write text files (default: %(default)s).",
    )
    parser.add_argument(
        "--overwrite",
        action="store_true",
        help="Overwrite destination files if they already exist.",
    )
    return parser.parse_args()


@lru_cache(maxsize=1)
def load_dictionary() -> set[str]:
    path = Path("/usr/share/dict/words")
    if not path.is_file():
        return set()
    text = path.read_text(encoding="utf-8", errors="ignore")
    return {line.strip().lower() for line in text.splitlines() if line.strip()}


def strip_punctuation(token: str) -> tuple[str, str, str]:
    start = 0
    end = len(token)
    while start < end and token[start] in PUNCT_CHARS:
        start += 1
    while end > start and token[end - 1] in PUNCT_CHARS:
        end -= 1
    return token[:start], token[start:end], token[end:]


def match_dictionary(word: str, dictionary: set[str]) -> bool:
    if not word:
        return False
    lower_word = word.lower()
    if lower_word in dictionary:
        return True
    for suffix in KNOWN_SUFFIXES:
        if lower_word.endswith(suffix) and len(lower_word) > len(suffix):
            base = lower_word[: -len(suffix)]
            if base in dictionary:
                return True
    return False


def candidate_score(word: str, dictionary: set[str]) -> int:
    trimmed = word.strip("'\"")
    if match_dictionary(trimmed, dictionary):
        return len(trimmed) + 10
    parts = [segment for token in WORD_SPLIT_RE.split(trimmed) for segment in token.split(".") if segment]
    if parts and all(match_dictionary(part, dictionary) for part in parts):
        return len(trimmed) + 5
    return 0


def fallback_ligature(prefix: str, suffix: str) -> str:
    lowered_prefix = prefix.lower()
    lowered_suffix = suffix.lower()

    if lowered_suffix.startswith(("ing",)) and lowered_prefix.endswith(("shu", "stu")):
        return "ffl"

    if lowered_suffix.startswith(("’s", "s’", "man", "mann", "son", "ford", "forda", "fordab", "ord", "orda", "offs", "off")) \
            or lowered_prefix.endswith(("ho", "je", "kee", "buf", "o", "self", "drop", "trade", "stu", "anyway—", "half-", "loose-")):
        return "ff"

    if ("www" in lowered_prefix and lowered_suffix.startswith(("at", "it"))) or lowered_suffix.startswith((
        "lift", "line", "lines", "lag", "lagging", "ledg", "ledged", "ledgling", "loor", "luid", "lue", "luent",
        "uenc", "uent", "lation", "lating", "lated", "flag", "flagging", "flat", "fleet", "flex", "fold")):
        return "fl"

    if lowered_suffix.startswith((
        "ing", "ed", "es", "ies", "ied", "ified", "ifies", "ifier", "ifiers", "ifying", "iation", "iations",
        "ience", "iency", "ient", "iable", "iary", "iaries", "ially", "ic", "ical", "ically", "ization", "izers", "ized", "izing",
        "gured", "guration", "gures", "ified—", "ified.", "ified,", "ident", "idents",
    )):
        return "fi"

    if lowered_suffix.startswith(("uced", "ucing", "uenced", "uencing", "uential", "uential—even")):
        return "fl"

    return "fi"


def adjust_replacement_case(replacement: str, prefix: str, suffix: str) -> str:
    letters = "".join(ch for ch in (prefix + suffix) if ch.isalpha())
    if letters and letters.isupper():
        return replacement.upper()
    return replacement


def restore_ligatures(content: str) -> str:
    dictionary = load_dictionary()

    def replacer(match: re.Match[str]) -> str:
        token = match.group(0)
        lead, core, trail = strip_punctuation(token)
        if "\x00" not in core:
            return token
        prefix, suffix = core.split("\x00", 1)

        best_score = -1
        best_candidate = prefix + "fi" + suffix
        best_replacement = "fi"

        for candidate in CANDIDATE_LIGATURES:
            candidate_word = prefix + candidate + suffix
            score = candidate_score(candidate_word, dictionary)
            if score > best_score:
                best_score = score
                best_candidate = candidate_word
                best_replacement = candidate

        if best_score <= 0:
            best_replacement = fallback_ligature(prefix, suffix)
            best_candidate = prefix + best_replacement + suffix

        best_replacement = adjust_replacement_case(best_replacement, prefix, suffix)
        return f"{lead}{prefix}{best_replacement}{suffix}{trail}"

    return re.sub(r"\S*\x00\S*", replacer, content)


def sanitize_text(content: str) -> str:
    if not content:
        return ""

    content = restore_ligatures(content)

    replacements = {
        "\u00a0": " ",  # non-breaking space
        "\u00ad": "",   # soft hyphen
        "\ufb01": "fi",
        "\ufb02": "fl",
    }

    for src, target in replacements.items():
        content = content.replace(src, target)

    content = content.replace("\x00", "")
    content = content.replace("\r\n", "\n").replace("\r", "\n")

    cleaned_lines: list[str] = []
    for raw_line in content.splitlines():
        line = re.sub(r"[ \t]+", " ", raw_line).strip()
        if not line:
            if cleaned_lines and cleaned_lines[-1] == "":
                continue
            cleaned_lines.append("")
        else:
            cleaned_lines.append(line)

    return "\n".join(cleaned_lines).strip()


def convert_pdf(pdf_path: Path, output_dir: Path, overwrite: bool) -> Path | None:
    reader = PdfReader(str(pdf_path))
    text_chunks = []
    for page in reader.pages:
        content = page.extract_text()
        cleaned = sanitize_text(content)
        if cleaned:
            text_chunks.append(cleaned)

    if not text_chunks:
        sys.stderr.write(f"No extractable text found in {pdf_path.name}, skipping.\n")
        return None

    output_dir.mkdir(parents=True, exist_ok=True)
    output_path = output_dir / f"{pdf_path.stem}.txt"
    if output_path.exists() and not overwrite:
        sys.stderr.write(f"Skipping existing file {output_path} (use --overwrite to replace).\n")
        return None

    output_path.write_text("\n\n".join(text_chunks), encoding="utf-8")
    return output_path


def main() -> None:
    args = parse_args()

    if not args.source.exists():
        sys.stderr.write(f"Source directory not found: {args.source}\n")
        sys.exit(1)

    pdf_files = sorted(path for path in args.source.glob("*.pdf") if path.is_file())
    if not pdf_files:
        sys.stderr.write(f"No PDF files found under {args.source}\n")
        sys.exit(0)

    converted = 0
    for pdf_file in pdf_files:
        result = convert_pdf(pdf_file, args.destination, args.overwrite)
        if result:
            converted += 1
            print(f"Wrote {result}")

    print(f"Completed conversion for {converted} file(s).")


if __name__ == "__main__":
    main()
