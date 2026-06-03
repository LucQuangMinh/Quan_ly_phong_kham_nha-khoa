import os

target = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\holidays.html"
source = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\doctors.html"

with open(source, 'r', encoding='utf-8') as f:
    doc_html = f.read()

start_style = doc_html.find('<style>') + len('<style>')
end_style = doc_html.find('</style>')
core_css = doc_html[start_style:end_style]

with open(target, 'r', encoding='utf-8') as f:
    holidays_html = f.read()

# Insert core_css right after <style> in holidays.html
holidays_start_style = holidays_html.find('<style>') + len('<style>')

# Remove <link rel="stylesheet" href="index.css" /> because it doesn't exist
holidays_html = holidays_html.replace('<link rel="stylesheet" href="index.css" />', '')

new_holidays_html = holidays_html[:holidays_start_style] + "\n" + core_css + "\n" + holidays_html[holidays_start_style:]

with open(target, 'w', encoding='utf-8') as f:
    f.write(new_holidays_html)

print("Successfully injected core CSS into holidays.html")
