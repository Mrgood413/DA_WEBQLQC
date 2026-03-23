# -*- coding: utf-8 -*-
from pathlib import Path

path = Path(__file__).resolve().parents[1] / "src/main/resources/templates/menu.html"
lines = path.read_text(encoding="utf-8").splitlines(keepends=True)

new_mid = """<!-- Filter Chips -->
<div id="category-chips" class="flex gap-3 mb-10 overflow-x-auto pb-2 no-scrollbar"></div>
<div id="menu-loading" class="text-center py-16 text-on-surface-variant">Đang tải thực đơn...</div>
<div id="menu-error" class="hidden text-center py-16 text-error"></div>
<div id="menu-grid" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8 min-h-[200px]"></div>
<script src="/js/menu-page.js"></script>
"""

# Keep lines 1-107 (index 0-106), drop 107-294, append from line 295 (index 294) = </main>
out = "".join(lines[:107]) + new_mid + "".join(lines[295:])
path.write_text(out, encoding="utf-8")
print("patched", path, "lines", len(lines), "->", len(out.splitlines()))
