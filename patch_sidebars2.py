import os
import glob
import re

html_files = glob.glob(r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\*.html')

for fpath in html_files:
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Xóa link prices.html trong thẻ a
    # Tìm đoạn: <a href="prices.html" class="nav-item" data-menu="prices"> ... </a>
    pattern_a = re.compile(r'<a href="prices\.html".*?</a>', re.DOTALL)
    content = pattern_a.sub('', content)

    # Xóa 'prices' khỏi allowedMenus
    # Tìm 'prices', hoặc "prices",
    content = content.replace("'prices', ", "")
    content = content.replace(", 'prices'", "")
    content = content.replace("'prices'", "")

    with open(fpath, 'w', encoding='utf-8') as f:
        f.write(content)

print(f"Patched {len(html_files)} html files.")
