from __future__ import annotations

import json
import os
import shlex
import subprocess
from typing import Any

import requests
from flask import Flask, redirect, render_template, request, url_for

app = Flask(__name__)


def engine_base_url() -> str:
    return os.getenv("DOCPROC_ENGINE_URL", "http://127.0.0.1:8080")


def swing_command() -> str:
    return os.getenv("DOCPROC_SWING_COMMAND", "mvn exec:java -Dexec.args=--gui")


def fetch_text(path: str, fallback: str = "") -> str:
    try:
        response = requests.get(f"{engine_base_url()}{path}", timeout=4)
        if response.ok:
            return response.text
    except requests.RequestException:
        return fallback
    return fallback


def fetch_json(path: str, fallback: dict[str, Any]) -> dict[str, Any]:
    try:
        response = requests.get(f"{engine_base_url()}{path}", timeout=4)
        if response.ok:
            return response.json()
    except (requests.RequestException, json.JSONDecodeError):
        return fallback
    return fallback


def post_form(path: str, data: dict[str, str]) -> bool:
    try:
        response = requests.post(f"{engine_base_url()}{path}", data=data, timeout=5)
        return response.status_code in (200, 302, 303)
    except requests.RequestException:
        return False


def parse_title_and_preview(document_text: str) -> tuple[str, str]:
    lines = document_text.splitlines()
    title = "Smart Document Editor"
    if lines and lines[0].startswith("#"):
        title = lines[0].lstrip("#").strip() or title
    return title, document_text


def extract_first_paragraph(document_text: str) -> str:
    for line in document_text.splitlines():
        stripped = line.strip()
        if not stripped:
            continue
        if stripped.startswith("#"):
            continue
        if stripped.startswith("[") and stripped.endswith("]"):
            continue
        if stripped.startswith("|"):
            continue
        if stripped.startswith("!["):
            continue
        return stripped
    return ""


@app.get("/")
def home() -> str:
    health_text = fetch_text("/health", "DOWN")
    is_up = health_text.strip().upper() == "OK"

    document_text = fetch_text("/document", "")
    title, preview = parse_title_and_preview(document_text)
    first_paragraph = extract_first_paragraph(document_text)

    word_count = fetch_json("/word-count", {"wordCount": 0}).get("wordCount", 0)
    unknown_words = fetch_json("/spell-check", {"unknownWords": []}).get("unknownWords", [])

    return render_template(
        "index.html",
        engine_url=engine_base_url(),
        engine_up=is_up,
        title=title,
        first_paragraph=first_paragraph,
        preview=preview,
        word_count=word_count,
        unknown_words=unknown_words,
    )


@app.post("/action/update-title")
def update_title() -> str:
    post_form("/update-title", {"title": request.form.get("title", "")})
    return redirect(url_for("home"))


@app.post("/action/update-paragraph")
def update_paragraph() -> str:
    post_form("/update-paragraph", {"text": request.form.get("text", "")})
    return redirect(url_for("home"))


@app.post("/action/add-paragraph")
def add_paragraph() -> str:
    post_form("/add-paragraph", {"text": request.form.get("text", "")})
    return redirect(url_for("home"))


@app.post("/action/open-swing")
def open_swing() -> str:
    command = shlex.split(swing_command())
    try:
        subprocess.Popen(command)
    except OSError:
        pass
    return redirect(url_for("home"))


if __name__ == "__main__":
    port = int(os.getenv("PORT", "5000"))
    app.run(host="0.0.0.0", port=port)
