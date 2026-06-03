import os
import re

base_dir = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static"

html_files = [f for f in os.listdir(base_dir) if f.endswith('.html')]

for filename in html_files:
    filepath = os.path.join(base_dir, filename)
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. We injected the script block with:
    # <script>
    #    function setupDynamicUI() { ... }
    # </script>
    # </body>
    
    # We will search for `function setupDynamicUI() { ... }` and ensure it gets called.
    # The safest way is to add an event listener right below its definition.
    
    if "document.addEventListener('DOMContentLoaded', setupDynamicUI);" not in content:
        # Let's replace the end of the function. 
        # The function ends with:
        #         });
        #     }
        
        # We can just replace `function setupDynamicUI() {` with the definition,
        # but to be totally safe we replace `    function setupDynamicUI() {`... wait, 
        # let's just append it before `</script>\n</body>` if we know that's how we injected it.
        # Note: some files might have other things before `</body>`.
        # Let's use regex to find `function setupDynamicUI() { ... }` and inject the caller.
        
        # Simpler: just inject `<script>document.addEventListener('DOMContentLoaded', setupDynamicUI);</script>` before `</body>`
        if "</body>" in content:
            content = content.replace("</body>", "<script>document.addEventListener('DOMContentLoaded', setupDynamicUI);</script>\n</body>")
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Patched {filename}")

print("Sidebar bug fixed successfully!")
